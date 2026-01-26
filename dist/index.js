"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const matchCluster = {
    br1: "americas", la1: "americas", la2: "americas", na1: "americas",
    oc1: "sea", eun1: "europe", euw1: "europe", ru: "europe", tr1: "europe",
    kr: "asia", jp1: "asia",
};
function formatTimeAgo(timestamp) {
    const diff = Date.now() - timestamp;
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    if (days > 0)
        return `Hace ${days} d`;
    if (hours > 0)
        return `Hace ${hours} h`;
    if (minutes > 0)
        return `Hace ${minutes} m`;
    return "Ahora";
}
async function fetchRiot(url) {
    const apiKey = process.env.RIOT_API_KEY;
    const res = await fetch(url, { headers: { "X-Riot-Token": apiKey || "" } });
    if (!res.ok)
        return null;
    return res.json();
}
async function getCoreData(invocador, region, count = 10) {
    const [name, tag] = (invocador || "").split("#");
    const cluster = matchCluster[region] || "europe";
    const account = await fetchRiot(`https://${cluster}.api.riotgames.com/riot/account/v1/accounts/by-riot-id/${encodeURIComponent(name)}/${encodeURIComponent(tag)}`);
    if (!account)
        return null;
    const sum = await fetchRiot(`https://${region}.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/${account.puuid}`);
    const matchIds = await fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/by-puuid/${account.puuid}/ids?count=${count}`) || [];
    const matches = (await Promise.all(matchIds.map((id) => fetchRiot(`https://${cluster}.api.riotgames.com/lol/match/v5/matches/${id}`))))
        .filter((m) => m && m.info);
    return { account, sum, matches };
}
const app = (0, express_1.default)();
app.use(express_1.default.json());
app.get("/api/dashboard", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 10);
        if (!data)
            return res.status(404).json({ error: "Jugador no encontrado" });
        let totalK = 0, totalD = 0, totalA = 0, totalWins = 0, totalGold = 0, totalCS = 0, totalDurationSec = 0;
        let bestKdaRatio = -1;
        let bestKdaStr = "0/0/0";
        const champs = {};
        let currentStreak = 0;
        let streakBroken = false;
        const partidasProcesadas = data.matches.map((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            totalK += p.kills;
            totalD += p.deaths;
            totalA += p.assists;
            totalGold += p.goldEarned;
            totalCS += (p.totalMinionsKilled + p.neutralMinionsKilled);
            totalDurationSec += m.info.gameDuration;
            if (p.win) {
                totalWins++;
                if (!streakBroken)
                    currentStreak++;
            }
            else {
                streakBroken = true;
            }
            const currentRatio = (p.kills + p.assists) / Math.max(1, p.deaths);
            if (currentRatio > bestKdaRatio) {
                bestKdaRatio = currentRatio;
                bestKdaStr = `${p.kills}/${p.deaths}/${p.assists}`;
            }
            if (!champs[p.championName])
                champs[p.championName] = { n: p.championName, p: 0, w: 0 };
            champs[p.championName].p++;
            if (p.win)
                champs[p.championName].w++;
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
                kdaPromedio: parseFloat(((totalK + totalA) / Math.max(1, totalD)).toFixed(2)),
                csPorMin: parseFloat(avgCS),
                oroPromedio: Math.round(totalGold / Math.max(1, data.matches.length)),
                rachaVictorias: currentStreak,
                tasaVictorias: data.matches.length > 0 ? Math.round((totalWins / data.matches.length) * 100) : 0,
                nivel: data.sum.summonerLevel,
                mejorKda: bestKdaStr,
                duracionPromedio: `${Math.floor((totalDurationSec / Math.max(1, data.matches.length)) / 60)}m`
            },
            companeros: [],
            campeones: Object.values(champs).map((c) => ({
                nombre: c.n, partidas: c.p, winRate: Math.round((c.w / c.p) * 100),
                imagen: `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${c.n}_0.jpg`
            })).sort((a, b) => b.partidas - a.partidas),
            partidas: partidasProcesadas.slice(0, 5)
        });
    }
    catch (e) {
        return res.status(500).json({ error: e.message });
    }
});
app.get("/api/historial", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 15);
        if (!data)
            return res.json([]);
        return res.json(data.matches.map((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            return {
                id: m.metadata.matchId, campeon: p.championName, resultado: p.win ? "VICTORIA" : "DERROTA",
                kda: `${p.kills}/${p.deaths}/${p.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`,
                hace: formatTimeAgo(m.info.gameCreation), icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${p.championName}.png`
            };
        }));
    }
    catch (e) {
        return res.json([]);
    }
});
app.get("/api/analisis", async (req, res) => {
    try {
        const { invocador, region } = req.query;
        const data = await getCoreData(invocador, region, 10);
        if (!data)
            return res.json({ kdaPorPartida: [], metricas: [], insights: [] });
        let totalKp = 0, totalVision = 0, totalDamage = 0, totalDuration = 0, wins = 0, totalCS = 0;
        const kdas = data.matches.map((m) => {
            const p = m.info.participants.find((x) => x.puuid === data.account.puuid);
            const team = m.info.teams.find((t) => t.teamId === p.teamId);
            const kp = team.objectives.champion.kills > 0 ? ((p.kills + p.assists) / team.objectives.champion.kills) * 100 : 0;
            totalKp += kp;
            totalVision += p.visionScore;
            totalDamage += p.totalDamageDealtToChampions;
            totalDuration += m.info.gameDuration;
            totalCS += (p.totalMinionsKilled + p.neutralMinionsKilled);
            if (p.win)
                wins++;
            return parseFloat(((p.kills + p.assists) / Math.max(1, p.deaths)).toFixed(2));
        }).reverse();
        const count = data.matches.length || 1;
        const winRate = (wins / count) * 100;
        const insights = [];
        if (winRate > 60)
            insights.push({ titulo: "Racha Ganadora", descripcion: "Tu tasa de victorias es excelente.", tipo: "POSITIVE" });
        return res.json({ kdaPorPartida: kdas, metricas: [], insights });
    }
    catch (e) {
        return res.status(500).json({ error: "Error de servidor" });
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
            if (!stats[p.championName])
                stats[p.championName] = { n: p.championName, p: 0, w: 0 };
            stats[p.championName].p++;
            if (p.win)
                stats[p.championName].w++;
        });
        return res.json(Object.values(stats).map((s) => ({
            nombre: s.n, partidas: s.p, winRate: Math.round((s.w / s.p) * 100),
            imagen: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${s.n}.png`
        })));
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
            throw new Error("Partida no encontrada");
        const pUser = m.info.participants.find((p) => (p.riotIdGameName + "#" + p.riotIdTagline).toLowerCase() === (invocador || "").toLowerCase()) || m.info.participants[0];
        const mapP = (p) => ({
            nombre: p.riotIdGameName ? `${p.riotIdGameName}#${p.riotIdTagline}` : p.summonerName,
            rol: p.teamPosition || "N/D",
            campeon: p.championName,
            kda: `${p.kills}/${p.deaths}/${p.assists}`,
            dano: (p.totalDamageDealtToChampions / 1000).toFixed(1) + "K"
        });
        const myTeamId = pUser.teamId;
        const blueTeamKills = m.info.participants.filter((p) => p.teamId === 100).reduce((s, p) => s + p.kills, 0);
        const redTeamKills = m.info.participants.filter((p) => p.teamId === 200).reduce((s, p) => s + p.kills, 0);
        return res.json({
            id: partidaId, campeonPrincipal: pUser.championName, resultado: pUser.win ? "VICTORIA" : "DERROTA",
            kda: `${pUser.kills}/${pUser.deaths}/${pUser.assists}`, duracion: `${Math.floor(m.info.gameDuration / 60)}m`,
            icono: `https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${pUser.championName}.png`,
            aliados: m.info.participants.filter((p) => p.teamId === myTeamId).map(mapP),
            enemigos: m.info.participants.filter((p) => p.teamId !== myTeamId).map(mapP),
            metricasGlobales: [
                { titulo: "Kills Totales", valor: `${blueTeamKills} - ${redTeamKills}` },
                { titulo: "Oro Total", valor: (m.info.participants.reduce((s, p) => s + p.goldEarned, 0) / 1000).toFixed(1) + "K" }
            ]
        });
    }
    catch (e) {
        return res.status(500).json({ error: e.message });
    }
});
app.get("/api/noticias", (_req, res) => res.json([
    { id: "1", titulo: "Parche 14.2", descripcion: "Buffs a asesinos.", imagen: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Zed_0.jpg", url: "https://lolesports.com" },
    { id: "2", titulo: "MSI 2024", descripcion: "Torneo esperado.", imagen: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/LeeSin_0.jpg", url: "https://lolesports.com" }
]));
exports.default = app;
//# sourceMappingURL=index.js.map