# TP2 Web Soubaibi

Application de chat du [TP2 de Richard Grin](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/), réalisée avec Jakarta Faces, PrimeFaces, LangChain4j et Gemini.

## Compiler et déployer

Utilisez le JDK 25, Maven et Payara 7 (Jakarta EE 11). Définissez la variable d’environnement `GEMINI_KEY` dans l’environnement du processus Payara avant de démarrer le domaine. Si le domaine est déjà démarré, arrêtez-le et relancez-le depuis cet environnement. Gardez la clé hors du code, de Git et des journaux.

Exécutez les commandes suivantes depuis la racine du projet.

```sh
mvn clean verify
asadmin start-domain domain1
asadmin deploy --contextroot tp2-web-soubaibi target/tp2-web-soubaibi.war
```

Ouvrez [le chat](http://localhost:8080/tp2-web-soubaibi/) sur le port HTTP de votre domaine. Sous Windows, `asadmin.bat` se trouve dans le répertoire `bin` de Payara.

## Fonctionnement

`index.xhtml` envoie les actions au backing bean CDI `Bb`, de portée vue (`@ViewScoped`). `Bb` délègue les appels à `LlmClient`, de portée `@Dependent`. Celui-ci crée le service `Assistant` avec `AiServices`, le modèle `gemini-3.5-flash-lite` et une mémoire LangChain4j de 20 messages par vue. Le rôle choisi est transmis comme `SystemMessage`. Le changement de rôle et le bouton **Nouvelle conversation** effacent la mémoire et le transcript.

## Vérifier dans le navigateur

1. En mode **Assistant**, donnez un code fictif et demandez-le au tour suivant.
2. Cliquez sur **Nouvelle conversation**, puis demandez le code de nouveau. Le rôle sélectionné est conservé et le code est oublié.
3. Sélectionnez **Traducteur** et envoyez « bonjour ». La réponse doit proposer une traduction anglaise.
4. Passez au **Coach de débat scientifique** et soumettez un argument à discuter. Vérifiez que le transcript précédent a disparu et que la réponse examine les preuves.
5. Sur un domaine lancé sans `GEMINI_KEY`, envoyez un message. La page doit afficher une erreur et conserver la saisie pour un nouvel essai.

Vérifié le 18 novembre 2026 avec `mvn clean verify` et Payara 7.2026.9. Les trois rôles, la mémoire, la réinitialisation et le comportement sans clé ont été testés dans Chrome.
