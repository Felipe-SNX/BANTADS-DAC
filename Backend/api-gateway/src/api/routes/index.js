const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

// Importa os roteadores de cada domínio
const clientesRoutes = require('./clientes.routes.js');
const gerentesRoutes = require('./gerentes.routes.js');

const router = Router();

const contasServiceProxy = createProxyMiddleware({
    target: process.env.MS_CONTA_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const newPath = '/contas' + path; 
        console.log(`[Proxy PathRewrite] Path reescrito para: ${newPath}`);
        return newPath;
    },
});

const clientesServiceProxy = createProxyMiddleware({
    target: process.env.MS_CONTA_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const newPath = '/clientes' + path;
        console.log(`[Proxy PathRewrite] Path reescrito para: ${newPath}`);
        return newPath;
    },
});

const gerentesServiceProxy = createProxyMiddleware({
    target: process.env.MS_GERENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const newPath = '/gerentes' + path;
        console.log(`[Proxy PathRewrite] Path reescrito para: ${newPath}`);
        return newPath;
    },
});

router.use('/clientes', clientesServiceProxy);
router.use('/contas', contasServiceProxy);
router.use('/gerentes', gerentesServiceProxy);

module.exports = router;