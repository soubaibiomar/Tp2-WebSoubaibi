package ma.emsi.soubaibi.tp2web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.soubaibi.tp2web.llm.LlmClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("bb")
@ViewScoped
public class Bb implements Serializable {

    private static final long serialVersionUID = 1L;

    private String question = "";
    private String roleSysteme = "assistant";

    @Inject
    private LlmClient llmClient;

    private final List<MessageChat> conversation = new ArrayList<>();

    // Envoyer une question depuis la page JSF
    public void envoyer() {
        if (question == null || question.isBlank()) {
            return;
        }

        String texte = question.trim();
        try {
            if (conversation.isEmpty()) {
                llmClient.setSystemRole(switch (roleSysteme) {
                    case "traducteur" -> """
                            Traduis le français en anglais et l'anglais en français.
                            Pour un texte de un à trois mots, donne aussi des exemples d'usage en anglais.
                            """;
                    case "coach" -> """
                            Tu es un coach de débat scientifique. Aide à formuler des arguments,
                            demande des preuves et distingue les faits des hypothèses.
                            """;
                    default -> "Tu es un assistant qui répond aux questions de manière claire et utile.";
                });
            }

            String reponse = llmClient.envoyer(texte);
            conversation.add(new MessageChat("Vous", texte, true));
            conversation.add(new MessageChat("Assistant", reponse, false));
            question = "";
        } catch (RuntimeException erreur) {
            System.getLogger(Bb.class.getName()).log(System.Logger.Level.WARNING,
                    "Échec de l’appel Gemini ({0})", erreur.getClass().getSimpleName());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Gemini n’a pas répondu.",
                    "Vérifiez la configuration du serveur ou réessayez."));
        }
    }

    // Réinitialiser la conversation
    public void nouvelleConversation() {
        conversation.clear();
        llmClient.setSystemRole(null);
        question = "";
    }

    // Changer le rôle sélectionné
    public void changerRole() {
        nouvelleConversation();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRoleSysteme() {
        return roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public List<MessageChat> getConversation() {
        return conversation;
    }

    public static class MessageChat implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String auteur;
        private final String texte;
        private final boolean utilisateur;

        public MessageChat(
                String auteur,
                String texte,
                boolean utilisateur
        ) {
            this.auteur = auteur;
            this.texte = texte;
            this.utilisateur = utilisateur;
        }

        public String getAuteur() {
            return auteur;
        }

        public String getTexte() {
            return texte;
        }

        public boolean isUtilisateur() {
            return utilisateur;
        }
    }
}
