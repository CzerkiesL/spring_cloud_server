#SpringCloudServer

Serveur Spring Cloud pouvant accueillir des micro-services contenant :
    - un serveur Gateway
    - un serveur de configuration
    - un serveur Eureka

##1. Technologies de base utilisées

- Java 25+
- Spring Boot 4+
- Maven 3.9+
- Docker 29+


##2. Structure du projet

###a. config-server :

**Rôle :**

Serveur de configuration centralisé pour tous les autres services (Gateway, Eureka, etc.).

Il fournit les fichiers de configuration (application.yml, etc.) aux autres micro-services au démarrage.

**Mode de récupération des configurations :**

Par défaut, le config-server récupère les configurations depuis un dépôt Git (GitHub, GitLab, etc.).

Il peut également être configuré pour lire les configurations :
    
    - depuis un dossier local sur le serveur
    - depuis un dossier présent sur ta machine, en précisant son chemin.

Comment changer la source de configuration
    
    - Ouvre le fichier de configuration du config-server :
        . config-server/src/main/resources/application.yml
    - Active le bon profil de configuration en cherchant la ligne :
        . spring.config.profiles.active
    - Modifie la valeur selon l’environnement que tu souhaites utiliser :
        . git pour récupérer les configurations depuis Git 
        . native pour récupérer les configurations depuis un dossier local.
    - Configure ensuite la source des configurations en cherchant la section :
        . spring.cloud.config.server
    - Selon le profil choisi :
        . configure l’URI Git 
        . le chemin local correspondant
    - Redémarre ensuite le config-server pour prendre en compte les changements

**Remarques :**

Les autres services (Gateway, user-service, etc.) doivent pointer vers le config-server via leur propre configuration.

Le dépôt ou dossier de configuration contient généralement des fichiers tels que :
    
    - application.yml ( configuration commune )
    - gateway-server.yml 
    - eureka-server.yml
    - autre-service.yml

**Dépendances :**

    - spring-boot-starter-actuator
    Permet d’exposer des endpoints de monitoring et de santé (/actuator/health, /actuator/info, etc.).

    - spring-cloud-config-server
    Fournit les fonctionnalités de serveur de configuration Spring Cloud.

    - spring-cloud-starter-bus-amqp
    Intègre Spring Cloud Bus avec RabbitMQ (AMQP) pour propager les changements de configuration aux autres services.

    - spring-cloud-config-monitor
    Permet de déclencher un refresh des configurations via des webhooks, par exemple depuis GitHub ou GitLab, lorsque le dépôt de configuration change.

    - spring-boot-starter-test
    Dépendance de test pour écrire et exécuter des tests unitaires et d’intégration.

    - spring-boot-starter-web
    Ajoute le support de Spring MVC et d’un serveur web embarqué, nécessaire pour exposer les endpoints HTTP du config-server.

###b. eureka-server

**Rôle :**

Serveur de découverte qui enregistre les différents micro-services et simplifie la communication entre eux.

**Fonctionnement : **

Chaque micro-service s’enregistre auprès du eureka-server au démarrage.
Il indique son nom de service, son adresse IP et son port.

Lorsqu’un service doit communiquer avec un autre, il interroge Eureka afin d’obtenir la liste des instances disponibles.

Cela permet :

    - de localiser dynamiquement les services
    - de supporter plusieurs instances d’un même service 
    - de mieux gérer les pannes ( une instance indisponible n’est plus renvoyée par Eureka )

**Configuration :**

Fichier de configuration :

    - eureka-server/src/main/resources/application.yml
        . Il contient principalement l’URL vers le config-server

    - eureka-server.yml repo git :
        . eureka.instance.hostname : nom d’hôte ou adresse IP du serveur Eureka 
        . eureka.client.register-with-eureka : vrai/faux, souvent false pour le serveur Eureka lui-même 
        . eureka.client.fetch-registry : vrai/faux 
        . eureka.server.* : paramètres avancés du serveur, renouvellement, seuils, etc

**Remarques :**

Les autres services doivent pointer vers le eureka-server via la propriété eureka.client.service-url.defaultZone.

Le eureka-server expose généralement une interface web permettant de visualiser les services enregistrés.

**Dépendances :**

    - spring-boot-starter-actuator
    Permet d’exposer des endpoints de monitoring et de santé (/actuator/health, /actuator/info, etc.).

    - spring-cloud-starter-config
    Permet à un service de récupérer sa configuration depuis un config-server.

    - spring-cloud-starter-netflix-eureka-server
    Transforme l’application en serveur Eureka.

    - spring-boot-starter-test
        Dépendance de test pour écrire et exécuter des tests unitaires et d’intégration.

###c. gateway-server

**Rôle :*

Passerelle API unique pour tous les clients. Elle permet d’envoyer les requêtes sur une même URL. 

La gateway se charge ensuite de router les requêtes vers le micro-service correspondant.

**Configuration :**

Fichier de configuration :

    - gateway-server/src/main/resources/application.yml il contient notamment :
        . l’URL vers le config-server
        . la définition des routes vers les micro-services 
        . le paramétrage des CORS 
        . les données sensibles ( secret des JWT )

    - application.yml repo git ( global ):
        . l’URL d’enregistrement sur Eureka 
        . l’enregistrement RabbitMQ 

    - gateway-server.yml repo git :
        . la mise en place de Resilience4j.

**Dépendances :**

    - spring-cloud-starter-gateway-server-webflux
    Fournit les fonctionnalités de Spring Cloud Gateway basées sur WebFlux : routage réactif, filtres, etc.

    - spring-cloud-starter-netflix-eureka-client
    Permet au service de s’enregistrer auprès du eureka-server et de découvrir les autres services via Eureka.

    - spring-boot-starter-actuator
    Permet d’exposer des endpoints de monitoring et de santé (/actuator/health, /actuator/info, /actuator/prometheus, etc.).

    - spring-cloud-starter-circuitbreaker-reactor-resilience4j
    Intègre Resilience4j avec Reactor pour gérer les pannes et la résilience : circuit breaker, retry, timeout, etc.

    - spring-boot-starter-oauth2-resource-server
    Configure l’application comme serveur de ressources OAuth2 : validation de tokens JWT, sécurité des endpoints, etc.

    - spring-cloud-starter-config
    Permet au service de récupérer sa configuration depuis le config-server au démarrage.

    - spring-boot-starter-test
    Dépendance de test pour écrire et exécuter des tests unitaires et d’intégration.

    - micrometer-registry-prometheus
    Expose les métriques Spring Boot au format Prometheus pour la supervision et le monitoring.

    - spring-cloud-starter-bus-amqp
    Intègre Spring Cloud Bus avec RabbitMQ (AMQP) pour propager les événements et les refresh de configuration entre les services.

    - spring-boot-starter-webflux
    Fournit la stack web réactive WebFlux utilisée par la gateway et les composants réactifs.

    - jjwt-api
    API principale de la bibliothèque Java JWT (JJWT) pour créer et valider des tokens JWT.

    - jjwt-impl
    Implémentation de JJWT utilisée à l’exécution pour la gestion des tokens JWT.

    - jjwt-jackson
    Module JJWT pour la sérialisation et la désérialisation JSON des claims JWT via Jackson.

    - spring-boot-configuration-processor
    Génère des métadonnées de configuration pour aider les IDE à proposer les propriétés personnalisées dans les fichiers application.yml et application.properties.

###d. Points importants

Un fichier .env est présent à la racine du projet. Il est ensuite importé dans chaque fichier application.yml des serveurs pour fournir les variables d’environnement.
Un fichier .env.example est disponible pour connaître les noms des variables nécessaires.

RabbitMQ est utilisé par le projet.

Un Dockerfile est présent dans chaque serveur. Il build d’abord le fichier .jar avec Maven, puis construit l’image Docker correspondant au serveur.

Un fichier docker-compose.yml prêt à l’emploi est disponible à la racine du projet.

⚠️ ***Ne jamais commit le fichier .env ***

##3. Pour commencer

###a. Cloner le repo

```bash
git clone https://github.com/CzerkiesL/spring_cloud_server.git
cd spring_cloud_server
```

###b. Build du projet

Depuis le dossier racine, lance la commande suivante :

```bash
mvn clean install
```

###c1. Lancement sans Docker Compose

Créer et paramétrer le fichier .env en respectant les noms de variables du fichier .env.example.

Lancer RabbitMQ :

```bash
docker run -d --name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
<<<<<<< HEAD
-e RABBITMQ_DEFAULT_USER=admin \
-e RABBITMQ_DEFAULT_PASS=admin \
=======
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=guest \
>>>>>>> 4446aeb (second commit)
rabbitmq:4-management
```

Lancer le config-server depuis la racine du projet :

```bash
cd config-server
mvn spring-boot:run
```

Lancer le eureka-server depuis la racine du projet :

```bash
cd eureka-server
mvn spring-boot:run
```

Lancer le gateway-server depuis la racine du projet :

```bash
cd gateway-server
mvn spring-boot:run
```

Lancer Prometheus, optionnel :

```bash
docker run -d --name prometheus \
-p 9090:9090 \
-v "$(pwd)/monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml" \
prom/prometheus
```

###c2. Lancement avec Docker Compose

Créer et paramétrer le fichier .env en respectant les noms de variables du fichier .env.example.

Depuis la racine du projet, lancer :

```bash
docker compose up --build
```

**Remarques :**

Vous pouvez activer ou désactiver le filtre JwtAuthenticationFilter dans le fichier SecurityConfig.java.

Si le config-server reste configuré pour Git, n’oubliez pas de créer ou renseigner le dépôt Git de configuration.

Si vous utilisez une configuration locale, renseignez correctement le chemin vers le dossier de configuration.

Pour les commandes contenant des ports, adaptez-les selon vos propres ports.

##4. Ajout de micro-services

Pour chaque nouveau micro-service, vérifier les points suivants :

    - ne pas oublier l’import du fichier .env et la connexion au config-server ;
    - vérifier l’enregistrement auprès du eureka-server ;
    - paramétrer les routes dans le fichier application.yml du gateway-server ;
    - si vous utilisez Docker, ajouter le conteneur correspondant dans le fichier docker-compose.yml ;
    - ne pas oublier d’ajouter un Dockerfile dans le service afin de construire son image Docker.

##5. Contact

<<<<<<< HEAD
**Développeur :** CzerkiesL
=======
**Développeur :** CzerkiesLudovic
>>>>>>> 4446aeb (second commit)

**Email :** [czerkies.ludovic.pro@outlook.fr](mailto:czerkies.ludovic.pro@outlook.fr)