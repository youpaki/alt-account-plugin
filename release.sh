#!/bin/bash

# Script automatisé pour créer une nouvelle version du plugin
# Usage: ./release.sh [major|minor|patch] [message]

set -e

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages colorés
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Vérifier les paramètres
VERSION_TYPE=${1:-patch}
COMMIT_MESSAGE=${2:-"Release version"}

if [[ ! "$VERSION_TYPE" =~ ^(major|minor|patch)$ ]]; then
    log_error "Type de version invalide: $VERSION_TYPE"
    log_info "Utilisez: major, minor, ou patch"
    exit 1
fi

log_info "Type de release: $VERSION_TYPE"

# Obtenir la version actuelle depuis pom.xml
CURRENT_VERSION=$(grep -o '<version>[^<]*</version>' pom.xml | head -1 | sed 's/<version>\(.*\)<\/version>/\1/')

if [ -z "$CURRENT_VERSION" ]; then
    log_error "Impossible de détecter la version depuis pom.xml"
    exit 1
fi

log_info "Version actuelle: $CURRENT_VERSION"

# Calculer la nouvelle version
IFS='.' read -r -a version_parts <<< "$CURRENT_VERSION"
major=${version_parts[0]}
minor=${version_parts[1]}
patch=${version_parts[2]}

case $VERSION_TYPE in
    major)
        major=$((major + 1))
        minor=0
        patch=0
        ;;
    minor)
        minor=$((minor + 1))
        patch=0
        ;;
    patch)
        patch=$((patch + 1))
        ;;
esac

NEW_VERSION="$major.$minor.$patch"
log_info "Nouvelle version: $NEW_VERSION"

# Confirmation
echo ""
log_warning "Voulez-vous créer la version $NEW_VERSION? [y/N]"
read -r confirm

if [[ ! $confirm == [yY] || ! $confirm == [yY][eE][sS] ]]; then
    if [[ $confirm != [yY] ]]; then
        log_info "Opération annulée"
        exit 0
    fi
fi

# 1. Mettre à jour la version dans pom.xml
log_info "Mise à jour de la version dans pom.xml..."
sed -i '' "s/<version>$CURRENT_VERSION<\/version>/<version>$NEW_VERSION<\/version>/" pom.xml
log_success "pom.xml mis à jour"

# 2. Mettre à jour CHANGELOG.md
log_info "Mise à jour du CHANGELOG.md..."
DATE=$(date +'%d %B %Y' | sed 's/January/janvier/; s/February/février/; s/March/mars/; s/April/avril/; s/May/mai/; s/June/juin/; s/July/juillet/; s/August/août/; s/September/septembre/; s/October/octobre/; s/November/novembre/; s/December/décembre/')

# Créer une section temporaire pour la nouvelle version
cat > changelog_temp.md << EOF
# Changelog - AltAccount Plugin

## Version $NEW_VERSION - $DATE

### 🔄 Changements
- $COMMIT_MESSAGE

EOF

# Ajouter le reste du changelog (en sautant la première ligne)
tail -n +2 CHANGELOG.md >> changelog_temp.md
mv changelog_temp.md CHANGELOG.md

log_success "CHANGELOG.md mis à jour"

# 3. Compiler le projet
log_info "Compilation du projet..."
if mvn clean package -q; then
    log_success "Compilation réussie"
else
    log_error "Échec de la compilation"
    exit 1
fi

# 4. Commit et tag
log_info "Commit des changements..."
git add pom.xml CHANGELOG.md
git commit -m "v$NEW_VERSION: $COMMIT_MESSAGE"
log_success "Changements commitées"

log_info "Création du tag v$NEW_VERSION..."
git tag -a "v$NEW_VERSION" -m "Version $NEW_VERSION: $COMMIT_MESSAGE"
log_success "Tag créé"

# 5. Push
log_info "Push vers GitHub..."
git push origin main
git push origin "v$NEW_VERSION"
log_success "Push réussi"

# 6. Créer la release GitHub avec le JAR
log_info "Création de la release GitHub..."
if command -v gh &> /dev/null && gh auth status &> /dev/null; then
    if ./create-release.sh "$NEW_VERSION"; then
        log_success "Release GitHub créée avec le JAR"
    else
        log_warning "Échec de la création de la release GitHub automatique"
        log_info "Vous pouvez la créer manuellement avec: ./create-release.sh $NEW_VERSION"
    fi
else
    log_warning "GitHub CLI non configuré"
    log_info "Installez GitHub CLI avec: brew install gh"
    log_info "Puis authentifiez-vous avec: gh auth login"
    log_info "Ensuite exécutez: ./create-release.sh $NEW_VERSION"
fi

echo ""
log_success "🎉 Version $NEW_VERSION créée avec succès!"
log_info "JAR compilé: target/alt-account-plugin-$NEW_VERSION.jar"
log_info "Tag: v$NEW_VERSION"

if command -v gh &> /dev/null && gh auth status &> /dev/null; then
    log_info "Release: https://github.com/$(gh repo view --json owner,name -q '.owner.login + "/" + .name')/releases/tag/v$NEW_VERSION"
fi