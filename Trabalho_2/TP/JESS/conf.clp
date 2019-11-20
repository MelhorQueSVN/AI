; Mapeamento de uma mensagem ACL para uma template JESS com os mesmos parâmetros 

(deftemplate ACLMessage
    (slot performative)
    (slot sender)
    (slot content)
    (slot lida (default 0))
    (slot time-to-live); usado para manter apenas a mensagem mais recente de um dado remetente
    (multislot receiver)
    (slot encoding)
    (slot reply-with)
    (slot in-reply-to)
    (slot envelope)
    (slot conversation-id)
    (slot protocol)
    (slot language)
    (slot ontology)
    (slot reply-by)
    (multislot reply-to)
)

; Template para um contador, utilizando para dar uma ordem temporal às mensagens ACL recebidas 

(deftemplate contador
    (slot ttl)
) 
(deffacts contador (contador (ttl 1)))  ; contador é inicializado a 1 


; Template para um MonitorAgent, sempre que um é inicializado, adiciona-se como um facto na base de conhecimento JESS. 

(deftemplate Agent 
    (slot agent)
)  


; Regra para inserir um facto em JESS sempre que for recebida uma mensagem ACL, criando um facto com formato ACL message em cima descrito 

(defrule jadeACL_para_jessACL
    ?msg <- (Message ?message) 
    ?c <- (contador (ttl ?count)) 
    => 
        (assert (ACLMessage 
                        (performative (call jade.lang.ACLMessage getPerformative (?message getPerformative)))       ; utiliza o java jade.lang.ACLMessage para chamar metodo getPerformative do JADE 
                        (sender             (?message getSender         )) 
                        (content            (?message getContent        )) 
                        (enconding          (?message getEnconding      )) 
                        (reply-with         (?message getReplyWith      )) 
                        (in-reply-to        (?message getInReplyTo      )) 
                        (envelope           (?message getEnvelope       )) 
                        (conversation-id    (?message getConversationId )) 
                        (protocol           (?message getProtocol       )) 
                        (language           (?message getLanguage       )) 
                        (ontology           (?message getOntology       )) 
                        (reply-by           (?message getReplyBy        )) 
                        (time-to-leave      ?count                      ) 

        )
    )
    (modify ?c (ttl (++ ?count)))   ; incrementa o contador por cada mensagem recebida 
    (printout t "M_JESS: Criado facto da mensagem enviada de " ((?message getSender) getName) "." crlf) 
    ; uma vez adicionada à base de conhecimento apaga-se esta estrutura auxiliar 
    (retract ?msg)
) 

; Devemos garantir que não existem factos ACLMessage repetidos no sistema 

(defrule elimina_rep_acl 
    ?m1 <- (ACLMessage (sender ?s1) (time-to-leave ?t1)) 
    ?m2 <- (ACLMessage (sender ?s2) (time-to-leave ?t2)) 
    => 
        (if (and (< ?t1 ?t2) (eq ?s1 ?s2)) then
            (retract ?s1)
        )
        (if (and (< ?t2 ?t1) (eq ?s1 ?s2)) then
            (retract ?s2)
        )
) 

; Função que é chamada através do TickerBehaviour para apresentar as estatísticas do sistema 

(deffunction informacoes () 
    (printout t "-----------------------STATS----------------------" crlf) 
    (facts) 
    (printout t "--------------------------------------------------" crlf)
)
