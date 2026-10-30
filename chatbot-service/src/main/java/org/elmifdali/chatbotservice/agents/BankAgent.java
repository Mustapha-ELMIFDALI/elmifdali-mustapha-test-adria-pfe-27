package org.elmifdali.chatbotservice.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class BankAgent {

    private static final Logger log = LoggerFactory.getLogger(BankAgent.class);

    private static final String SYSTEM_PROMPT = """
            Tu es l'assistant d'une banque. Tu réponds en français, de façon courte et claire.

            Règles :
            - Utilise toujours les outils pour obtenir les comptes et les transactions.
            - Ne donne jamais un identifiant, un solde ou un montant qui ne vient pas d'un outil.
            - Si l'utilisateur désigne un compte par son client ou son numéro (ex : compte 01), retrouve son id avec les outils.
            - Avant un paiement, un virement, ou le blocage d'un compte, résume l'opération et demande une confirmation.
              N'exécute l'opération qu'après un "oui" explicite de l'utilisateur.
            - Les identifiants ne sont pas conservés d'un message à l'autre : juste avant d'exécuter une opération,
              y compris après le "oui", rappelle l'outil getAllAccounts et utilise l'id exact qu'il renvoie.
            - Si un outil renvoie une erreur, explique-la simplement sans inventer de résultat.
            - Si la réponse n'est pas disponible avec les outils, réponds : **JE NE SAIS PAS**.
            """;

    // The failure can come after a tool already ran, so never tell the user that nothing happened
    private static final String UNAVAILABLE_MESSAGE = """
            L'assistant est momentanément indisponible. Si vous aviez confirmé une opération, \
            elle a peut-être été exécutée : vérifiez vos transactions avant de recommencer.""";

    private final ChatClient chatClient;

    public BankAgent(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(tools)
                .build();
    }

    public String chat(String query, String conversationId) {
        try {
            return chatClient.prompt()
                    .user(query)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .call()
                    .content();
        } catch (RuntimeException e) {
            log.error("Agent failed for conversation {}", conversationId, e);
            return UNAVAILABLE_MESSAGE;
        }
    }

    public Flux<String> chatStream(String query, String conversationId) {
        return chatClient.prompt()
                .user(query)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .onErrorResume(e -> {
                    log.error("Agent stream failed for conversation {}", conversationId, e);
                    return Flux.just(UNAVAILABLE_MESSAGE);
                });
    }
}
