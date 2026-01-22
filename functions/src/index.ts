import {setGlobalOptions} from "firebase-functions/v2";
import {onRequest} from "firebase-functions/v2/https";
import express, {Request, Response} from "express";

setGlobalOptions({ maxInstances: 10, timeoutSeconds: 60, secrets: ["RIOT_API_KEY"] });

type Region = "br1"|"eun1"|"euw1"|"jp1"|"kr"|"la1"|"la2"|"na1"|"oc1"|"ru"|"tr1";
const matchCluster: Record<string, string> = {
  br1: "americas", la1: "americas", la2: "americas", na1: "americas",
  oc1: "sea", eun1: "europe", euw1: "europe", ru: "europe", tr1: "europe",
  kr: "asia", jp1: "asia",
};

function formatTimeAgo(timestamp: number): string {
    const diff = Date.now() - timestamp;
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `Hace ${days} d`;
    if (hours > 0) return `Hace ${hours} h`;
    if (minutes > 0) return `Hace ${minutes} m`;
    return "Ahora";
}

async function fetchRiot(url: string) {
  const apiKey = process.env.RIOT_API_KEY;
  const res = await fetch(url, { headers: {"X-Riot-Token": apiKey || ""} });
  if (!res.ok) return null;
  return res.json();
}

async function getCoreData(invocador: string, region: Region, count: number = 10) {
    const [name, tag] = (invocador || "").split("#");
    const cluster = matchCluster[region] || "europe";
    const account = await fetchRiot(`https://${cluster}.api.riotgames.com/riot/account/v1/accounts/by-riot-id/${encodeURIComponent(name)}/${encodeURIComponent(tag)}`);
    if (!account) return null;
    const sum = await fetchRiot(`https://${region}.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/${account.puuid}`);
    const matchIds: string[] = await fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/by-puuid/${account.puuid}/ids?count=${count}`) || [];
    const matches = (await Promise.all(matchIds.map(id => fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/${id}`))))
                    .filter(m => m && m.info);
    return { account, sum, matches };
}

const app = express();
app.use(express.json());

app.get("/dashboard", async (req: Request, res: Response) => {
  try {
    const { invocador, region } = req.query as { invocador: string, region: Region };
    const data = await getCoreData(invocador, region, 10);
    if (!data) return res.status(404).json({error: "Jugador no encontrado"});

    let k=0, d=0, a=0, w=0, totalGold=0, totalCS=0, totalDurationSec=0;
    const champs: Record<string, any> = {};
    
    const partidas = data.matches.map(m => {
      const p = m.info.participants.find((x: any) => x.puuid === data.account.puuid);
      k+=p.kills; d+=p.deaths; a+=p.assists; if(p.win) w++;
      totalGold += p.goldEarned; totalCS += (p.totalMinionsKilled + p.neutralMinionsKilled);
      totalDurationSec += m.info.gameDuration;

      if(!champs[p.championName]) champs[p.championName] = {n: p.championName, p:0, w:0};
      champs[p.championName].p++; if(p.win) champs[p.championName].w++;

      return {
        id: m.metadata.matchId,
        campeon: p.championName,
        resultado: p.win ? "VICTORIA" : "DERROTA",
        kda: `${p.kills}/${p.deaths}/${p.assists}`,
        duracion: `${Math.floor(m.info.gameDuration / 60)}m`,
        hace: formatTimeAgo(m.info.gameCreation),
        icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${p.championName}.png`
      };
    });

    const avgCS = totalDurationSec > 0 ? (totalCS / (totalDurationSec / 60)).toFixed(1) : "0.0";

    return res.json({
      invocador: { id: data.sum.id, nombreInvocador: invocador, region },
      estadisticas: {
        kdaPromedio: parseFloat(((k + a) / Math.max(1, d)).toFixed(2)),
        csPorMin: parseFloat(avgCS), oroPromedio: Math.round(totalGold / Math.max(1, data.matches.length)),
        rachaVictorias: w, tasaVictorias: data.matches.length > 0 ? Math.round((w/data.matches.length)*100) : 0,
        nivel: data.sum.summonerLevel, mejorKda: `${k}/${d}/${a}`, duracionPromedio: `${Math.floor((totalDurationSec/Math.max(1, data.matches.length))/60)}m`
      },
      companeros: [],
      campeones: Object.values(champs).map((c:any) => ({
        nombre: c.n, partidas: c.p, winRate: Math.round((c.w/c.p)*100),
        imagen: `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${c.n}_0.jpg`
      })).sort((a,b)=>b.partidas-a.partidas),
      partidas: partidas.slice(0, 5)
    });
  } catch (e: any) { return res.status(500).json({error: e.message}); }
});

app.get("/historial", async (req: Request, res: Response) => {
    try {
        const { invocador, region } = req.query as { invocador: string, region: Region };
        const data = await getCoreData(invocador, region, 15);
        if(!data) return res.json([]);
        return res.json(data.matches.map(m => {
            const p = m.info.participants.find((x: any) => x.puuid === data.account.puuid);
            return {
                id: m.metadata.matchId, campeon: p.championName, resultado: p.win ? "VICTORIA" : "DERROTA",
                kda: `${p.kills}/${p.deaths}/${p.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`,
                hace: formatTimeAgo(m.info.gameCreation), icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${p.championName}.png`
            };
        }));
    } catch (e) { return res.json([]); }
});

app.get("/analisis", async (req: Request, res: Response) => {
    try {
        const { invocador, region } = req.query as { invocador: string, region: Region };
        const data = await getCoreData(invocador, region, 10);
        if(!data) return res.json({kdaPorPartida: [], metricas: []});
        
        let totalKp = 0, totalVision = 0, totalDamage = 0, totalDuration = 0, wins = 0;
        const kdas = data.matches.map(m => {
            const p = m.info.participants.find((x: any) => x.puuid === data.account.puuid);
            const team = m.info.teams.find((t: any) => t.teamId === p.teamId);
            
            const kp = team.objectives.champion.kills > 0 ? ((p.kills + p.assists) / team.objectives.champion.kills) * 100 : 0;
            totalKp += kp;
            totalVision += p.visionScore;
            totalDamage += p.totalDamageDealtToChampions;
            totalDuration += m.info.gameDuration;
            if(p.win) wins++;

            return parseFloat(((p.kills + p.assists) / Math.max(1, p.deaths)).toFixed(2));
        }).reverse();

        const count = data.matches.length || 1;
        return res.json({
            kdaPorPartida: kdas,
            metricas: [
                { titulo: "Tasa de Victoria", valor: `${Math.round((wins/count)*100)}%`, tendencia: wins >= 5 ? "Sube" : "Baja" },
                { titulo: "Participación", valor: `${Math.round(totalKp/count)}%`, tendencia: "Meta: 55%" },
                { titulo: "Visión /partida", valor: (totalVision/count).toFixed(1), tendencia: "Mejorable" },
                { titulo: "Daño /min", valor: (totalDamage / (totalDuration/60)).toFixed(0), tendencia: "Súper" }
            ]
        });
    } catch (e) { return res.status(500).json({ error: "Error de servidor" }); }
});

app.get("/campeones", async (req: Request, res: Response) => {
    try {
        const { invocador, region } = req.query as { invocador: string, region: Region };
        const data = await getCoreData(invocador, region, 20);
        if(!data) return res.json([]);
        const stats: Record<string, any> = {};
        data.matches.forEach(m => {
            const p = m.info.participants.find((x: any) => x.puuid === data.account.puuid);
            if(!stats[p.championName]) stats[p.championName] = {n: p.championName, p: 0, w: 0};
            stats[p.championName].p++; if(p.win) stats[p.championName].w++;
        });
        return res.json(Object.values(stats).map((s:any) => ({ nombre: s.n, partidas: s.p, winRate: Math.round((s.w/s.p)*100), imagen: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${s.n}.png` })));
    } catch (e) { return res.json([]); }
});

app.get("/detalle", async (req: Request, res: Response) => {
    try {
        const { partidaId, region, invocador } = req.query as { partidaId: string, region: Region, invocador: string };
        const cluster = matchCluster[region] || "europe";
        const m = await fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/${partidaId}`);
        if(!m) throw new Error("Partida no encontrada");
        const pUser = m.info.participants.find((p: any) => (p.riotIdGameName + "#" + p.riotIdTagline).toLowerCase() === (invocador || "").toLowerCase()) || m.info.participants[0];
        const mapP = (p: any) => ({ nombre: p.riotIdGameName ? `${p.riotIdGameName}#${p.riotIdTagline}` : p.summonerName, rol: p.teamPosition || "N/D", campeon: p.championName, kda: `${p.kills}/${p.deaths}/${p.assists}`, dano: (p.totalDamageDealtToChampions / 1000).toFixed(1) + "K" });
        return res.json({ id: partidaId, campeonPrincipal: pUser.championName, resultado: pUser.win ? "VICTORIA" : "DERROTA", kda: `${pUser.kills}/${pUser.deaths}/${pUser.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`, icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${pUser.championName}.png`, aliados: m.info.participants.filter((p: any) => p.teamId === pUser.teamId).map(mapP), enemigos: m.info.participants.filter((p: any) => p.teamId !== pUser.teamId).map(mapP), metricasGlobales: [] });
    } catch (e: any) { return res.status(500).json({ error: e.message }); }
});

app.get("/noticias", (_req, res) => res.json([
    { id: "1", titulo: "Parche 14.2", descripcion: "Buffs a asesinos y ajustes al mapa.", imagen: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Zed_0.jpg", url: "https://lolesports.com" }
]));

export const riotProxy = onRequest(app);
