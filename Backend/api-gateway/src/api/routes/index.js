const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const { verifyToken, checkRole } = require('../../middlewares/auth.middleware');
const router = Router();
const axios = require('axios');

const onProxyReqHandler = (proxyReq, req, res) => {
    if (req.body) {
        const bodyData = JSON.stringify(req.body);
        
        proxyReq.setHeader('Content-Type', 'application/json');
        proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
    
        proxyReq.write(bodyData);
    }
};

const orquestradorServiceProxy = createProxyMiddleware({
    target: process.env.MS_ORQUESTRADOR_URL,
    changeOrigin: true,
    logLevel: 'debug',
    on: { 
        proxyReq: onProxyReqHandler
    }
});

const clientesServiceProxy = createProxyMiddleware({
    target: process.env.MS_CLIENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        let finalPath = path;
        if (path === '/' || path.startsWith('/?')) {
            const [basePath, queryString] = path.split('?');
            finalPath = '/clientes' + (queryString ? '?' + queryString : '');
        }
        console.log(`[Proxy MS-Cliente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
    on: { 
        proxyReq: onProxyReqHandler
    }
});

const gerentesServiceProxy = createProxyMiddleware({
    target: process.env.MS_GERENTE_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const finalPath = '/gerentes' + (path === '/' ? '' : path);
        console.log(`[Proxy MS-Gerente] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
    on: { 
        proxyReq: onProxyReqHandler
    }
});

const contasServiceProxy = createProxyMiddleware({
    target: process.env.MS_CONTA_URL,
    changeOrigin: true,
    logLevel: 'debug',
    pathRewrite: (path, req) => {
        const finalPath = '/contas' + (path === '/' ? '' : path);
        console.log(`[Proxy MS-Conta] Original: "${req.originalUrl}", Reescrito para: "${finalPath}"`);
        return finalPath;
    },
    on: { 
        proxyReq: onProxyReqHandler
    }
});

const authServiceProxy = createProxyMiddleware({
    target: process.env.MS_AUTH_URL,
    changeOrigin: true,
    logLevel: 'debug',
    on: { 
        proxyReq: onProxyReqHandler
    }
});

router.get('/reboot', async (req, res, next) => {
    console.log("Iniciando chamada de REBOOT para todos os serviços...");

    const clienteUrl = `${process.env.MS_CLIENTE_URL}/clientes/reboot`;
    const contaUrl = `${process.env.MS_CONTA_URL}/contas/reboot`;
    const authUrl = `${process.env.MS_AUTH_URL}/auth/reboot`;
    const gerenteUrl = `${process.env.MS_GERENTE_URL}/gerentes/reboot`;

    try {
        const clienteRequest = axios.get(clienteUrl);
        const contaRequest = axios.get(contaUrl);
        const authRequest = axios.get(authUrl);
        const gerenteRequest = axios.get(gerenteUrl);

        await Promise.all([
            clienteRequest,
            gerenteRequest,
            contaRequest,
            authRequest
        ]);

        return res.status(200).json({ 
            message: "Banco de dados criado conforme especificação" 
        });

    } catch (error) {
        console.error(`FALHA NO REBOOT: Um serviço falhou.`, error.message);
        
        return res.status(500).json({ 
            message: "Erro ao sincronizar banco de dados",
            serviceError: error.message
        });
    }
});

router.post('/login', (req, res, next) => {
    req.url = '/auth/login';
    authServiceProxy(req, res, next);
});

router.post('/logout', verifyToken, (req, res, next) => {
    req.url = '/auth/logout';
    authServiceProxy(req, res, next);
});

router.post('/clientes', async (req, res, next) => {
    try {
        const { cpf } = req.body;

        if (!cpf) {
            return res.status(400).json({ message: "CPF é obrigatório." });
        }

        const clienteUrl = `${process.env.MS_CLIENTE_URL}/clientes/checkCpf/${cpf}`;
        await axios.get(clienteUrl);

        console.log("indo para a saga");
        req.url = '/saga/autocadastro';
        orquestradorServiceProxy(req, res, next);

    } catch (error) {
        if (error.response && error.response.status === 409) {
            return res.status(409).json(error.response.data);
        }

        next(error);
    }
});

router.put('/clientes/:cpf', verifyToken, (req, res, next) => {
    req.url = `/saga/alterarPerfil/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.post('/gerentes', verifyToken, checkRole(['ADMINISTRADOR']), (req, res, next) => {
    req.url = '/saga/inserirGerente';
    orquestradorServiceProxy(req, res, next);
});

router.delete('/gerentes/:cpf', verifyToken, checkRole(['ADMINISTRADOR']), (req, res, next) => {
    req.url = `/saga/removerGerente/${req.params.cpf}`;
    orquestradorServiceProxy(req, res, next);
});

router.put('/gerentes/:cpf', verifyToken, checkRole(['ADMINISTRADOR']), (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

router.post('/clientes/:cpf/rejeitar', verifyToken, checkRole(['GERENTE']), clientesServiceProxy);
router.post('/clientes/:cpf/aprovar', verifyToken, checkRole(['GERENTE']), clientesServiceProxy);

router.get('/clientes/:cpf', verifyToken, async (req, res, next) => {
    const { cpf } = req.params;
    const { authorization } = req.headers;

    const clienteUrl = `${process.env.MS_CLIENTE_URL}/clientes/${cpf}`;
    const contaUrl = `${process.env.MS_CONTA_URL}/contas/${cpf}/dadosConta`;

    try {
        const clienteRequest = axios.get(clienteUrl, {
            headers: { 'Authorization': authorization }
        });

        const contaRequest = axios.get(contaUrl, {
            headers: { 'Authorization': authorization }
        }).catch(error => {
            console.warn(`[Gateway] MS-Conta falhou para CPF ${cpf}: ${error.message}`);
            return null;
        });

        const [clienteResponse, contaResponse] = await Promise.all([
            clienteRequest,
            contaRequest
        ]);

        const clienteData = clienteResponse.data;

        const cpfGerente = (clienteData && clienteData.cpfGerente) ? clienteData.cpfGerente : null;
        console.log(cpfGerente);
        let gerenteRequest;
        if (cpfGerente) {
            const gerenteUrl = `${process.env.MS_GERENTE_URL}/gerentes/${cpfGerente}`;
            gerenteRequest = axios.get(gerenteUrl, {
                headers: { 'Authorization': authorization }
            }).catch(error => {
                console.warn(`[Gateway] MS-Gerente falhou para CPF ${cpfGerente}: ${error.message}`);
                return null;
            });
        } else {
            gerenteRequest = Promise.resolve(null);
        }

        const gerenteResponse = await gerenteRequest;

        const contaData = (contaResponse?.data) ? contaResponse.data : null;
        const gerenteData = (gerenteResponse?.data) ? gerenteResponse.data : null;

        const limite = contaData ? contaData.limite : null;
        const saldo = contaData ? contaData.saldo : null;
        const conta = contaData ? contaData.conta : null;

        const gerente = gerenteData ? gerenteData.cpf : null;
        const gerente_email = gerenteData ? gerenteData.email : null;
        const gerente_nome = gerenteData ? gerenteData.nome : null;

        const compositeResponse = {
            ...clienteData,
            limite: limite,
            conta: conta,
            saldo: saldo,
            gerente: gerente,
            gerente_nome: gerente_nome,
            gerente_email: gerente_email
        };

        res.status(200).json(compositeResponse);

    } catch (error) {
        console.error(`[Gateway] Erro CRÍTICO ao buscar dados primários do cliente ${cpf}:`, error.message);

        const status = error.response?.status || 500;
        res.status(status).json({
            message: 'Erro ao buscar dados primários do cliente.',
            serviceError: error.message
        });
    }
});

router.use('/gerentes', verifyToken, gerentesServiceProxy);
router.use('/clientes', verifyToken, clientesServiceProxy);
router.use('/contas', verifyToken, contasServiceProxy);

module.exports = router;