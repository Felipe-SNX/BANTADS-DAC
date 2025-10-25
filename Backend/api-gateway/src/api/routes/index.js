const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

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
    target: process.env.MS_CLIENTE_URL,
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
        //Isso está aqui, pois nas rotas sem parâmetros estava dando erro por causa da barra extra
        if (path === '/') {
            console.log(`[Proxy PathRewrite] Path (raiz) reescrito para: /gerentes`);
            return '/gerentes';
        }
        const newPath = '/gerentes' + path;
        console.log(`[Proxy PathRewrite] Path (sub-rota) reescrito para: ${newPath}`);
        return newPath;
    },
});

router.use('/gerentes', gerentesServiceProxy);
router.use('/clientes', clientesServiceProxy);
router.use('/contas', contasServiceProxy);

module.exports = router;