const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const router = Router();

const contasServiceProxy = createProxyMiddleware({
    target: process.env.MS_CONTA_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const [pathOnly, queryString] = path.split('?');
        const query = queryString ? `?${queryString}` : '';

        let newPath;

        if (pathOnly === '/') {
            newPath = '/contas';
        } else {
            newPath = '/contas' + pathOnly;
        }

        const finalPath = newPath + query;

        console.log(`[Proxy PathRewrite] Original: "${path}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
});

const clientesServiceProxy = createProxyMiddleware({
    target: process.env.MS_CLIENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const [pathOnly, queryString] = path.split('?');
        const query = queryString ? `?${queryString}` : '';

        let newPath;

        if (pathOnly === '/') {
            newPath = '/clientes';
        } else {
            newPath = '/clientes' + pathOnly;
        }

        const finalPath = newPath + query;

        console.log(`[Proxy PathRewrite] Original: "${path}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
});

const gerentesServiceProxy = createProxyMiddleware({
    target: process.env.MS_GERENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const [pathOnly, queryString] = path.split('?');
        const query = queryString ? `?${queryString}` : '';

        let newPath;

        if (pathOnly === '/') {
            newPath = '/gerentes';
        } else {
            newPath = '/gerentes' + pathOnly;
        }

        const finalPath = newPath + query;

        console.log(`[Proxy PathRewrite] Original: "${path}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
});

router.use('/gerentes', gerentesServiceProxy);
router.use('/clientes', clientesServiceProxy);
router.use('/contas', contasServiceProxy);

module.exports = router;