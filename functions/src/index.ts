/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

import {setGlobalOptions} from "firebase-functions/v2";
import {onRequest} from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import express, {Request, Response, NextFunction} from "express";

setGlobalOptions({
  maxInstances: 10,
  secrets: ["RIOT_API_KEY"],
});

type Region = "br1"|"eun1"|"euw1"|"jp1"|"kr"|"la1"|"la2"|"na1"|"oc1"|"ru"|"tr1";

const matchCluster: Record<Region, string> = {
  br1: "americas",
  la1: "americas",
  la2: "americas",
  na1: "americas",
  oc1: "sea",
  eun1: "europe",
  euw1: "europe",
  ru: "europe",
  tr1: "europe",
  kr: "asia",
  jp1: "asia",
};

function platformUrl(region: Region) {
  return `https://${region}.api.riotgames.com`;
}
function matchUrl(region: Region) {
  const cluster = matchCluster[region] ?? "europe";
  return `https://${cluster}.api.riotgames.com`;
}

function getApiKey(): string {
  const key = process.env.RIOT_API_KEY || (global as any).process?.env.RIOT_API_KEY;
  if (!key) {
    throw new Error("Configura la variable de entorno RIOT_API_KEY (firebase functions:secrets:set RIOT_API_KEY).");
  }
  return key;
}

async function fetchJson(url: string) {
  const apiKey = getApiKey();
  const res = await fetch(url, {
    headers: {
      "X-Riot-Token": apiKey,
    },
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Error ${res.status}: ${text}`);
  }
  return res.json();
}

async function obtenerSummoner(region: Region, nombre: string) {
  return fetchJson(`${platformUrl(region)}/lol/summoner/v4/summoners/by-name/${encodeURIComponent(nombre)}`);
}

async function obtenerRank(region: Region, summonerId: string) {
  return fetchJson(`${platformUrl(region)}/lol/league/v4/entries/by-summoner/${summonerId}`);
}

async function obtenerPartidas(region: Region, puuid: string, count = 10) {
  const clusterBase = matchUrl(region);
  const ids: string[] = await fetchJson(`${clusterBase}/lol/match/v5/matches/by-puuid/${puuid}/ids?count=${count}`);
  const detalles = await Promise.all(ids.map(async (id) => {
    const match = await fetchJson(`${clusterBase}/lol/match/v5/matches/${id}`);
    return {id, match};
  }));
  return detalles;
}

function mapResultado(win: boolean) {
  return win ? "VICTORIA" : "DERROTA";
}

const app = express();
app.use(express.json());

const router = express.Router();

router.get("/dashboard", async (req: Request, res: Response, _next: NextFunction) => {
  try {
    const invocador = (req.query.invocador as string) ?? "";
    const region = (req.query.region as Region) ?? "euw1";
    if (!invocador) return res.status(400).json({error: "Falta invocador"});

    const summoner = await obtenerSummoner(region, invocador);
    const rank = await obtenerRank(region, summoner.id);
    const partidas = await obtenerPartidas(region, summoner.puuid, 5);
    const tasas = rank?.[0];

    const respuesta = {
      invocador: {
        id: summoner.id,
        correo: "",
        nombreInvocador: invocador,
      },
      estadisticas: {
        kdaPromedio: 0,
        csPorMin: 0,
        oroPromedio: 0,
        duracionPromedio: "0m",
        rachaVictorias: 0,
        mejorKda: "0/0/0",
        tasaVictorias: tasas?.wins && tasas?.losses ? Math.round((tasas.wins / (tasas.wins + tasas.losses)) * 100) : 0,
        nivel: summoner.summonerLevel ?? 0,
      },
      companeros: [] as any[],
      campeones: [] as any[],
      partidas: partidas.map(({id, match}) => {
        const p = match.info?.participants?.find((x: any) => x.puuid === summoner.puuid) ?? match.info?.participants?.[0];
        return {
          id,
          campeon: p?.championName ?? "Desconocido",
          resultado: mapResultado(!!p?.win),
          kda: `${p?.kills ?? 0}/${p?.deaths ?? 0}/${p?.assists ?? 0}`,
          duracion: `${Math.round((match.info?.gameDuration ?? 0) / 60)}m`,
          hace: "",
          icono: "",
        };
      }),
    };
    return res.json(respuesta);
  } catch (e: any) {
    logger.error("riotProxy/dashboard", e);
    return res.status(500).json({error: e.message ?? "Error inesperado"});
  }
});

router.get("/historial", async (req: Request, res: Response, _next: NextFunction) => {
  try {
    const invocador = (req.query.invocador as string) ?? "";
    const region = (req.query.region as Region) ?? "euw1";
    if (!invocador) return res.status(400).json({error: "Falta invocador"});

    const summoner = await obtenerSummoner(region, invocador);
    const partidas = await obtenerPartidas(region, summoner.puuid, 10);
    const respuesta = partidas.map(({id, match}) => {
      const p = match.info?.participants?.find((x: any) => x.puuid === summoner.puuid) ?? match.info?.participants?.[0];
      return {
        id,
        campeon: p?.championName ?? "Desconocido",
        resultado: mapResultado(!!p?.win),
        kda: `${p?.kills ?? 0}/${p?.deaths ?? 0}/${p?.assists ?? 0}`,
        duracion: `${Math.round((match.info?.gameDuration ?? 0) / 60)}m`,
        hace: "",
        icono: "",
      };
    });
    return res.json(respuesta);
  } catch (e: any) {
    logger.error("riotProxy/historial", e);
    return res.status(500).json({error: e.message ?? "Error inesperado"});
  }
});

router.get("/analisis", async (req: Request, res: Response, _next: NextFunction) => {
  try {
    const invocador = (req.query.invocador as string) ?? "";
    const region = (req.query.region as Region) ?? "euw1";
    if (!invocador) return res.status(400).json({error: "Falta invocador"});
    const summoner = await obtenerSummoner(region, invocador);
    const partidas = await obtenerPartidas(region, summoner.puuid, 10);
    const kdaPorPartida = partidas.map(({match}) => {
      const p = match.info?.participants?.find((x: any) => x.puuid === summoner.puuid) ?? match.info?.participants?.[0];
      const deaths = p?.deaths || 1;
      return ((p?.kills ?? 0) + (p?.assists ?? 0)) / deaths;
    });
    const respuesta = {
      kdaPorPartida,
      metricas: [
        {titulo: "Tasa de Victoria Reciente", valor: "N/D"},
        {titulo: "KDA Promedio", valor: (kdaPorPartida.reduce((a, b) => a + b, 0) / (kdaPorPartida.length || 1)).toFixed(1)},
        {titulo: "CS/min", valor: "N/D"},
        {titulo: "Duración Media", valor: "N/D"},
        {titulo: "Racha más Larga", valor: "N/D"},
        {titulo: "Mejor KDA", valor: (Math.max(...kdaPorPartida) || 0).toFixed(1)},
        {titulo: "Mejor Campeón", valor: "N/D"},
        {titulo: "Daño por Partida", valor: "N/D"},
      ],
    };
    return res.json(respuesta);
  } catch (e: any) {
    logger.error("riotProxy/analisis", e);
    return res.status(500).json({error: e.message ?? "Error inesperado"});
  }
});

router.get("/campeones", async (req: Request, res: Response, _next: NextFunction) => {
  try {
    const invocador = (req.query.invocador as string) ?? "";
    const region = (req.query.region as Region) ?? "euw1";
    if (!invocador) return res.status(400).json({error: "Falta invocador"});
    const summoner = await obtenerSummoner(region, invocador);
    const partidas = await obtenerPartidas(region, summoner.puuid, 20);
    const conteo: Record<string, number> = {};
    partidas.forEach(({match}) => {
      const p = match.info?.participants?.find((x: any) => x.puuid === summoner.puuid) ?? match.info?.participants?.[0];
      if (!p?.championName) return;
      conteo[p.championName] = (conteo[p.championName] || 0) + 1;
    });
    const listado = Object.entries(conteo)
        .map(([nombre, partidas]) => ({
          nombre,
          partidas,
          winRate: 0,
          kda: "",
          imagen: `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${nombre}_0.jpg`,
        }))
        .sort((a, b) => b.partidas - a.partidas);
    return res.json(listado);
  } catch (e: any) {
    logger.error("riotProxy/campeones", e);
    return res.status(500).json({error: e.message ?? "Error inesperado"});
  }
});

router.get("/noticias", async (_req: Request, res: Response) => {
  // Mock simple, luego se puede enlazar a RSS u otro servicio
  return res.json([
    {
      id: "1",
      titulo: "T1 gana la LCK",
      descripcion: "Resumen rápido del último split.",
      imagen: "https://images.unsplash.com/photo-1508672019048-805c876b67e2?auto=format&fit=crop&w=1600&q=80",
      url: "https://lolesports.com/",
    },
    {
      id: "2",
      titulo: "MSI: previa y equipos",
      descripcion: "Todo lo que necesitas saber del próximo MSI.",
      imagen: "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=1600&q=80",
      url: "https://lolesports.com/",
    },
  ]);
});

// Monta rutas en / y en /riotProxy para compatibilidad
app.use("/", router);
app.use("/riotProxy", router);

export const riotProxy = onRequest(app);
