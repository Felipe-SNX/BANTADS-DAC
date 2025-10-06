const { Router } = require('express');
const proxy = require('express-http-proxy');

const router = Router();

const clientesServiceUrl = 'http://localhost:5001'; 

const clientesProxy = proxy(clientesServiceUrl, {
    
    proxyReqPathResolver: function (req) {
        const newPath = `/clientes${req.url}`;
        
        console.log(`[Proxy] Rota original: ${req.originalUrl} -> Rota de destino no serviço: ${newPath}`);
        
        return newPath;
    },

    userResDecorator: function(proxyRes, proxyResData, userReq, userRes) {            
        const bodyString = proxyResData.toString('utf8');
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
            console.error("Erro ao manipular resposta:", e);
            return proxyResData;
        }
    }
});

const clientesListarCpfProxy = proxy(clientesServiceUrl, {
    
    proxyReqPathResolver: function (req) {
        const newPath = `/clientes${req.url}`;
        
        console.log(`[Proxy] Rota original: ${req.originalUrl} -> Rota de destino no serviço: ${newPath}`);
        
        return newPath;
    },

    userResDecorator: function(proxyRes, proxyResData, userReq, userRes) {            
        const bodyString = proxyResData.toString('utf8');
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

            modifiedResponse = transformarCliente(bodyJson);
            return JSON.stringify(modifiedResponse, null, 2);

        } catch(e) {
            console.error("Erro ao manipular resposta:", e);
            return proxyResData;
        }
    }
});

router.get('/:cpf', clientesListarCpfProxy);
router.put('/:cpf', clientesListarCpfProxy);
router.get('/', clientesProxy);

module.exports = router;