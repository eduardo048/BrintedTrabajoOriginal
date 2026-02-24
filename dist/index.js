"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
// @ts-ignore
const cors_1 = __importDefault(require("cors"));
const matchCluster = {
    br1: "americas", la1: "americas", la2: "americas", na1: "americas",
    oc1: "sea", eun1: "europe", euw1: "europe", ru: "europe", tr1: "europe",
    kr: "asia", jp1: "asia",
};
const cache = new Map();
const CACHE_TIME = 300 * 1000;
async function fetchRiot(url) {
    const apiKey = process.env.RIOT_API_KEY;
    if (!apiKey)
        throw new Error("Falta API KEY");
    const res = await fetch(url, { headers: { "X-Riot-Token": apiKey } });
    return res.ok ? await res.json() : null;
}
async function getCoreData(invocador, region, count = 20) {
    const cacheKey = `${invocador}-${region}-${count}`;
    const cached = cache.get(cacheKey);
    if (cached && cached.exp > Date.now())
        return cached.data;
    const [name, tag] = (invocador || "").split("#");
    const cluster = matchCluster[region] || "europe";
    const account = await fetchRiot(`https://${cluster}.api.riotgames.com/riot/account/v1/accounts/by-riot-id/${encodeURIComponent(name)}/${encodeURIComponent(tag)}`);
    if (!account?.puuid)
        return null;
    const sum = await fetchRiot(`https://${region}.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/${account.puuid}`);
    const matchIds = await fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/by-puuid/${account.puuid}/ids?count=${count}`) || [];
    const matches = (await Promise.all(matchIds.map((id) => fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/${id}`))))
        .filter((m) => m?.info);
    const result = { account, sum, matches };
    cache.set(cacheKey, { data: result, exp: Date.now() + CACHE_TIME });
    return result;
}
const app = (0, express_1.default)();
app.use((0, cors_1.default)());
app.use(express_1.default.json());
app.get("/api/dashboard", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 10);
        if (!data)
            return res.status(404).json({ error: "No encontrado" });
        let tK = 0, tD = 0, tA = 0, tW = 0, tG = 0, tCS = 0, tDur = 0;
        let bestKdaRatio = -1, bestKdaStr = "0/0/0";
        const champs = {};
        data.matches.forEach((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            if (!p)
                return;
            tK += p.kills;
            tD += p.deaths;
            tA += p.assists;
            tG += p.goldEarned;
            tCS += (p.totalMinionsKilled + p.neutralMinionsKilled);
            tDur += m.info.gameDuration;
            if (p.win)
                tW++;
            const ratio = (p.kills + p.assists) / Math.max(1, p.deaths);
            if (ratio > bestKdaRatio) {
                bestKdaRatio = ratio;
                bestKdaStr = `${p.kills}/${p.deaths}/${p.assists}`;
            }
            if (!champs[p.championName])
                champs[p.championName] = { n: p.championName, p: 0, w: 0 };
            champs[p.championName].p++;
            if (p.win)
                champs[p.championName].w++;
        });
        // Calcular racha de victorias actual
        let rachaActual = 0;
        for (const m of data.matches) {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            if (p && p.win)
                rachaActual++;
            else
                break;
        }
        const count = data.matches.length || 1;
        return res.json({
            invocador: { id: data.sum.id, nombreInvocador: invocador, region },
            estadisticas: {
                kdaPromedio: parseFloat(((tK + tA) / Math.max(1, tD)).toFixed(2)),
                csPorMin: parseFloat((tCS / Math.max(1, tDur / 60)).toFixed(1)),
                oroPromedio: Math.round(tG / count),
                rachaVictorias: rachaActual, tasaVictorias: Math.round((tW / count) * 100),
                nivel: data.sum.summonerLevel || 0, mejorKda: bestKdaStr, duracionPromedio: `${Math.floor((tDur / count) / 60)}m`
            },
            campeones: Object.values(champs).map((c) => ({
                nombre: c.n, partidas: c.p, winRate: Math.round((c.w / c.p) * 100),
                imagen: `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${c.n}_0.jpg`
            })).sort((a, b) => b.partidas - a.partidas),
            partidas: data.matches.map((m) => {
                const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
                return { id: m.metadata.matchId, campeon: p.championName, resultado: p.win ? "VICTORIA" : "DERROTA", kda: `${p.kills}/${p.deaths}/${p.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`, hace: "Reciente", icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${p.championName}.png` };
            }).slice(0, 5)
        });
    }
    catch (e) {
        return res.status(500).json({ error: e.message });
    }
});
app.get("/api/analisis", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 10);
        if (!data)
            return res.json({ kdaPorPartida: [], metricas: [], insights: [] });
        let tK = 0, tD = 0, tA = 0, tCS = 0, tDur = 0, tDmg = 0, tW = 0;
        const kdas = data.matches.map((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            if (!p)
                return 0;
            tK += p.kills;
            tD += p.deaths;
            tA += p.assists;
            tDmg += p.totalDamageDealtToChampions;
            tCS += (p.totalMinionsKilled + p.neutralMinionsKilled);
            tDur += m.info.gameDuration;
            if (p.win)
                tW++;
            return parseFloat(((p.kills + p.assists) / Math.max(1, p.deaths)).toFixed(2));
        }).reverse();
        const count = data.matches.length || 1;
        const wr = Math.round((tW / count) * 100);
        const avgCs = parseFloat((tCS / Math.max(1, tDur / 60)).toFixed(1));
        const avgKda = parseFloat(((tK + tA) / Math.max(1, tD)).toFixed(2));
        const insights = [];
        if (wr < 50)
            insights.push({ titulo: "Racha Baja", descripcion: "Analiza tus repeticiones para ver fallos tácticos.", tipo: "NEGATIVE" });
        else
            insights.push({ titulo: "Buen Ritmo", descripcion: "Mantienes un winrate positivo, sigue así.", tipo: "POSITIVE" });
        if (avgCs < 6)
            insights.push({ titulo: "Mejorar Farm", descripcion: "Tu media de CS/Min es baja. Prioriza súbditos.", tipo: "NEUTRAL" });
        if (avgKda > 3)
            insights.push({ titulo: "Supervivencia", descripcion: "Tienes un KDA excelente, buena toma de decisiones.", tipo: "POSITIVE" });
        return res.json({
            kdaPorPartida: kdas,
            metricas: [
                { titulo: "Tasa de Victoria", valor: `${wr}%` },
                { titulo: "KDA Medio", valor: avgKda.toString() },
                { titulo: "CS/Min", valor: avgCs.toString() },
                { titulo: "Daño Medio", valor: Math.round(tDmg / count).toLocaleString() }
            ],
            insights
        });
    }
    catch (e) {
        return res.status(500).json({ error: "Error" });
    }
});
app.get("/api/campeones", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 20);
        if (!data)
            return res.json([]);
        const stats = {};
        data.matches.forEach((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            if (p) {
                if (!stats[p.championName])
                    stats[p.championName] = { n: p.championName, p: 0, w: 0, k: 0, d: 0, a: 0 };
                stats[p.championName].p++;
                if (p.win)
                    stats[p.championName].w++;
                stats[p.championName].k += p.kills;
                stats[p.championName].d += p.deaths;
                stats[p.championName].a += p.assists;
            }
        });
        return res.json(Object.values(stats).map((s) => ({
            nombre: s.n, partidas: s.p, winRate: Math.round((s.w / s.p) * 100), kda: `${(s.k / s.p).toFixed(1)}/${(s.d / s.p).toFixed(1)}/${(s.a / s.p).toFixed(1)}`,
            imagen: `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${s.n}_0.jpg`
        })).sort((a, b) => b.partidas - a.partidas));
    }
    catch (e) {
        return res.json([]);
    }
});
app.get("/api/detalle", async (req, res) => {
    try {
        const { partidaId, region, invocador } = req.query;
        const cluster = matchCluster[region] || "europe";
        const m = await fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/${partidaId}`);
        if (!m)
            return res.status(404).json({ error: "No encontrada" });
        const pU = m.info.participants.find((p) => (p.riotIdGameName + "#" + p.riotIdTagline).toLowerCase() === (invocador || "").toLowerCase()) || m.info.participants[0];
        const mapP = (p) => ({ nombre: p.riotIdGameName ? `${p.riotIdGameName}#${p.riotIdTagline}` : p.summonerName, rol: p.teamPosition || "N/D", campeon: p.championName, kda: `${p.kills}/${p.deaths}/${p.assists}`, dano: (p.totalDamageDealtToChampions / 1000).toFixed(1) + "K" });
        const kills100 = m.info.participants.filter((p) => p.teamId === 100).reduce((s, p) => s + p.kills, 0);
        const kills200 = m.info.participants.filter((p) => p.teamId === 200).reduce((s, p) => s + p.kills, 0);
        const oroTotal = m.info.participants.reduce((s, p) => s + p.goldEarned, 0);
        return res.json({
            id: partidaId, campeonPrincipal: pU.championName, resultado: pU.win ? "VICTORIA" : "DERROTA", kda: `${pU.kills}/${pU.deaths}/${pU.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`, icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${pU.championName}.png`,
            aliados: m.info.participants.filter((p) => p.teamId === pU.teamId).map(mapP),
            enemigos: m.info.participants.filter((p) => p.teamId !== pU.teamId).map(mapP),
            metricasGlobales: [
                { titulo: "Kills Totales", valor: `${kills100} - ${kills200}` },
                { titulo: "Oro de Partida", valor: (oroTotal / 1000).toFixed(1) + "K" }
            ]
        });
    }
    catch (e) {
        return res.status(500).json({ error: "Error" });
    }
});
app.get("/api/noticias", (_req, res) => res.json([
    { id: "1", titulo: "Temporada 2024", descripcion: "Nuevos cambios en el mapa y objetos míticos eliminados.", imagen: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Aatrox_0.jpg", url: "https://lolesports.com" },
    { id: "2", titulo: "LEC Invierno", descripcion: "G2 Esports domina la fase regular con una racha increíble.", imagen: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Yone_0.jpg", url: "https://lolesports.com" },
    { id: "3", titulo: "Notas del Parche 14.3", descripcion: "Ajustes a campeones de la jungla y mejoras a tiradores.", imagen: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Jinx_0.jpg", url: "https://lolesports.com" }
]));
app.get("/api/historial", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 15);
        return res.json((data?.matches || []).map((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            return { id: m.metadata.matchId, campeon: p.championName, resultado: p.win ? "VICTORIA" : "DERROTA", kda: `${p.kills}/${p.deaths}/${p.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`, hace: "Reciente", icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${p.championName}.png` };
        }));
    }
    catch (e) {
        return res.json([]);
    }
});
exports.default = app;
//# sourceMappingURL=index.js.map