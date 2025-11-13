const { Router } = require('express');
const axios = require('axios');
const router = Router();

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

module.exports = router;