# Utilisation de l'IA pendant le TP2 — Partie Web

**Problème rencontré :**

Pendant les tests de mon application JSF sur Payara 7, certaines requêtes envoyées à Gemini avec LangChain4j expiraient. Le modèle utilisé au départ, `gemini-3.5-flash`, venait de l'exemple du cours. La page de chat s'affichait, mais le modèle ne répondait pas dans le délai prévu.

**Pourquoi j'ai utilisé l'IA :**

J'ai demandé de l'aide à ChatGPT, puis j'ai utilisé le mode Work pour poursuivre les tests sur le projet. Je voulais savoir si le délai venait du modèle Gemini, de la configuration de LangChain4j ou de la façon dont la page JSF appelait le service.

**Question posée à l'IA (reformulée) :**

« Mon application JSF utilise LangChain4j pour communiquer avec Gemini, mais certaines requêtes expirent pendant les tests sur Payara. Comment identifier l'origine du problème et le résoudre tout en respectant les consignes du professeur ? »

**Réponse de l'IA :**

Work a testé la réponse de Gemini avec des appels directs à l'API, en dehors de l'application. Le modèle initial a aussi dépassé le délai lors d'un essai direct, tandis que `gemini-3.5-flash-lite` a répondu. Cela a montré que le problème pouvait se produire même sans passer par JSF.

Work a remplacé le modèle dans `LlmClient.java` par `gemini-3.5-flash-lite` et a conservé le délai de 30 secondes déjà configuré dans LangChain4j. Ensuite, Work a recompilé le projet avec Maven, redéployé le WAR sur Payara 7 et vérifié dans le navigateur que le chat recevait des réponses de Gemini.

**Ce que j'ai appris :**

J'ai compris qu'il faut choisir un modèle adapté et fixer un délai d'attente explicite pour les appels à l'API. Tester le service externe séparément m'aide à distinguer un problème de réponse de l'API d'une erreur dans mon application.
