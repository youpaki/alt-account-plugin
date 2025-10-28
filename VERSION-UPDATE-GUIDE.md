# Guide de mise à jour des versions

## Pour mettre à jour la version du plugin :

### 1. Mettre à jour le pom.xml
```xml
<version>X.Y.Z</version>
```

### 2. Le plugin.yml est automatique
Le `plugin.yml` utilise `${project.version}` donc pas besoin de modification manuelle.

### 3. Mettre à jour le CHANGELOG.md
Ajouter une nouvelle section en haut du fichier avec :
- Numéro de version
- Date
- Liste des changements

### 4. Recompiler
```bash
mvn clean package
```

### 5. Vérifier la version dans le JAR
```bash
unzip -q -c target/alt-account-plugin-X.Y.Z.jar plugin.yml | grep version
```

## Exemple de versioning

- **X.Y.Z** où :
  - **X** = Version majeure (changements incompatibles)
  - **Y** = Version mineure (nouvelles fonctionnalités)
  - **Z** = Patch (corrections de bugs)

### Versions actuelles :
- `1.0.0` - Version initiale
- `1.0.1` - Corrections de désérialisation
- `1.1.0` - Persistance des alts + système de skins

### Prochaines versions suggérées :
- `1.1.1` - Corrections mineures
- `1.2.0` - Nouvelles fonctionnalités
- `2.0.0` - Refactoring majeur ou changements incompatibles