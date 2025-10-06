const { Router } = require('express');
const proxy = require('express-http-proxy');

const router = Router();
const gerentesServiceUrl = 'http://localhost:5003';

const gerentesProxy = proxy(gerentesServiceUrl, {
    userResDecorator: function(proxyRes, proxyResData, userReq, userRes) {
        const bodyString = proxyResData.toString('utf8');
        console.log('[userResDecorator] Resposta recebida do serviço:', bodyString);

        try {
            let bodyJson = JSON.parse(bodyString);
            let modifiedResponse;

            const transformarCliente = (cliente) => {
                if (!cliente || typeof cliente !== 'object') return cliente; 
                return {
                    cpf: cliente.cpf,
                    nome: cliente.nome,
                    email: cliente.email,
                    endereco: cliente.endereco ? `${cliente.endereco.logradouro}, nr ${cliente.endereco.numero}` : 'Endereço não informado',
                    cidade: cliente.endereco ? cliente.endereco.cidade : 'Não informado',
                    estado: cliente.endereco ? cliente.endereco.estado : 'Não informado'
                };
            };

            if (Array.isArray(bodyJson)) {
                modifiedResponse = bodyJson.map(transformarCliente);
            } else {
                modifiedResponse = transformarCliente(bodyJson);
            }

            return JSON.stringify(modifiedResponse, null, 2);

        } catch(e) {
            console.error("erro", e);
            return proxyResData;
        }
    }
});

router.use('/', gerentesProxy);

module.exports = router;