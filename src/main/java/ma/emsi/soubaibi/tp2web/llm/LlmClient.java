package ma.emsi.soubaibi.tp2web.llm;

import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;

import java.io.Serializable;
import java.time.Duration;

@Dependent
public class LlmClient implements Serializable {
    private static final long serialVersionUID = 1L;

    private String systemRole;
    // Seuls les messages confirmés survivent à la passivation de la vue ou à un appel en échec.
    private String historique;
    private transient Assistant assistant;
    private transient ChatMemory chatMemory;

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        historique = null;
        if (chatMemory != null) {
            chatMemory.clear();
            if (systemRole != null) {
                chatMemory.add(SystemMessage.from(systemRole));
            }
        }
    }

    public String envoyer(String prompt) {
        if (assistant == null) {
            initialiser();
        }

        try {
            String reponse = assistant.chat(prompt);
            if (reponse == null || reponse.isBlank()) {
                throw new IllegalStateException("Réponse Gemini vide");
            }
            historique = ChatMessageSerializer.messagesToJson(chatMemory.messages());
            return reponse;
        } catch (RuntimeException erreur) {
            assistant = null;
            chatMemory = null;
            throw erreur;
        }
    }

    private void initialiser() {
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null || cle.isBlank()) {
            throw new IllegalStateException("GEMINI_KEY manquante");
        }

        var modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(0)
                .logRequestsAndResponses(false)
                .build();

        chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        if (historique != null) {
            ChatMessageDeserializer.messagesFromJson(historique).forEach(chatMemory::add);
        } else if (systemRole != null) {
            chatMemory.add(SystemMessage.from(systemRole));
        }

        assistant = AiServices.builder(Assistant.class)
                .chatModel(modele)
                .chatMemory(chatMemory)
                .build();
    }
}
