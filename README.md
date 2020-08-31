# developers-api project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Scaffold the application

You can run your application in dev mode that enables live coding using:
```
mvn io.quarkus:quarkus-maven-plugin:1.7.1.Final:create -DprojectGroupId=com.demo.api.developers -DprojectArtifactId=developers-api  -DclassName=DevelopersResource
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Injecting JSON's support

If your endpoints work with JSON format y“ou will need the quarkus-resteasy-jsonb or quarkus-resteasy-jackson extension in your project”
```
./mvnw quarkus:add-extension -Dextensions="quarkus-resteasy-jackson"
```

## Dependency  injection

Add @Inject annotation on the component needs be injected. The component being injected must be anotated with @ApplicationScoped

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `developers-api-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/developers-api-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/developers-api-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
