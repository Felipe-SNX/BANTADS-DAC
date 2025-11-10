//Arquivo para os proxies para evitar que o código fique completamente no arquivo principal

const { createProxyMiddleware } = require('http-proxy-middleware');

const onProxyReqHandler = (proxyReq, req, res) => {
    if (req.body) {
        const bodyData = JSON.stringify(req.body);
        
        proxyReq.setHeader('Content-Type', 'application/json');
        proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
        
        proxyReq.write(bodyData);
    }
};

const createServiceProxy = (targetUrl, pathRewrite) => {
    return createProxyMiddleware({
        target: targetUrl,
        changeOrigin: true,
        logLevel: 'debug',
        on: {
            proxyReq: onProxyReqHandler
        },
        ...(pathRewrite && { pathRewrite })
    });
};


const orquestradorServiceProxy = createServiceProxy(process.env.MS_ORQUESTRADOR_URL);
const authServiceProxy = createServiceProxy(process.env.MS_AUTH_URL);

const clientesServiceProxy = createServiceProxy(
    process.env.MS_CLIENTE_URL,
    (path, req) => {
        if (path === '/' || path.startsWith('/?')) {
            const [basePath, queryString] = path.split('?');
            
            const finalPath = '/clientes' + (queryString ? '?' + queryString : '');
            
            console.log(`[Proxy MS-Cliente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
            return finalPath;
        }

        const finalPath = '/clientes' + path;
        
        console.log(`[Proxy MS-Cliente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    }
);

const gerentesServiceProxy = createServiceProxy(
    process.env.MS_GERENTE_URL,
    (path, req) => {
        let finalPath = path;
        if (!path.startsWith('/gerentes')) {
            const newBasePath = (path === '/' ? '' : path);
            finalPath = '/gerentes' + newBasePath;
        }
        console.log(`[Proxy MS-Gerente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    }
);

const contasServiceProxy = createServiceProxy(
    process.env.MS_CONTA_URL,
    (path, req) => {
        const finalPath = '/contas' + (path === '/' ? '' : path);
        console.log(`[Proxy MS-Conta] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    }
);

module.exports = {
    orquestradorServiceProxy,
    authServiceProxy,
    clientesServiceProxy,
    gerentesServiceProxy,
    contasServiceProxy
};