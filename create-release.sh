#!/bin/bash

# Script pour automatiquement créer une release GitHub avec le JAR compilé
# Usage: ./create-release.sh [version]

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

# Vérifier que gh CLI est installé
if ! command -v gh &> /dev/null; then
    log_error "GitHub CLI (gh) n'est pas installé."
    log_info "Installez-le avec: brew install gh"
    log_info "Puis authentifiez-vous avec: gh auth login"
    exit 1
fi

# Vérifier que l'utilisateur est authentifié
if ! gh auth status &> /dev/null; then
    log_error "Vous n'êtes pas authentifié avec GitHub CLI."
    log_info "Authentifiez-vous avec: gh auth login"
    exit 1
fi

# Obtenir la version actuelle depuis pom.xml
CURRENT_VERSION=$(grep -o '<version>[^<]*</version>' pom.xml | head -1 | sed 's/<version>\(.*\)<\/version>/\1/')

if [ -z "$CURRENT_VERSION" ]; then
    log_error "Impossible de détecter la version depuis pom.xml"
    exit 1
fi

# Utiliser la version fournie en paramètre ou celle du pom.xml
VERSION=${1:-$CURRENT_VERSION}
TAG="v$VERSION"

log_info "Version détectée: $VERSION"
log_info "Tag: $TAG"

# Vérifier que le JAR existe
JAR_FILE="target/alt-account-plugin-$VERSION.jar"

if [ ! -f "$JAR_FILE" ]; then
    log_warning "JAR non trouvé: $JAR_FILE"
    log_info "Compilation en cours..."
    
    if mvn clean package -q; then
        log_success "Compilation réussie"
    else
        log_error "Échec de la compilation"
        exit 1
    fi
fi

# Vérifier que le JAR existe maintenant
if [ ! -f "$JAR_FILE" ]; then
    log_error "JAR toujours introuvable après compilation: $JAR_FILE"
    exit 1
fi

# Vérifier que le tag existe
if ! git tag -l | grep -q "^$TAG$"; then
    log_error "Le tag $TAG n'existe pas."
    log_info "Créez le tag avec: git tag -a $TAG -m 'Version $VERSION'"
    exit 1
fi

# Vérifier que le tag est pushé
if ! git ls-remote --tags origin | grep -q "refs/tags/$TAG"; then
    log_error "Le tag $TAG n'est pas pushé sur origin."
    log_info "Pushez le tag avec: git push origin $TAG"
    exit 1
fi

# Générer les notes de release depuis CHANGELOG.md
RELEASE_NOTES_FILE="release-notes-$VERSION.tmp"

log_info "Génération des notes de release depuis CHANGELOG.md..."

# Extraire la section correspondante du CHANGELOG
if ! awk "/## Version $VERSION/,/## Version/" CHANGELOG.md | head -n -1 | tail -n +2 > "$RELEASE_NOTES_FILE"; then
    log_warning "Impossible d'extraire les notes depuis CHANGELOG.md"
    echo "Release automatique pour la version $VERSION" > "$RELEASE_NOTES_FILE"
    echo "" >> "$RELEASE_NOTES_FILE"
    echo "Consultez CHANGELOG.md pour les détails complets." >> "$RELEASE_NOTES_FILE"
fi

# Vérifier si la release existe déjà
if gh release view "$TAG" &> /dev/null; then
    log_warning "La release $TAG existe déjà."
    read -p "Voulez-vous la supprimer et la recréer? [y/N]: " confirm
    
    if [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]]; then
        log_info "Suppression de la release existante..."
        gh release delete "$TAG" --yes
        log_success "Release supprimée"
    else
        log_info "Opération annulée"
        rm -f "$RELEASE_NOTES_FILE"
        exit 0
    fi
fi

# Créer la release GitHub avec le JAR
log_info "Création de la release GitHub $TAG..."

if gh release create "$TAG" \
    --title "AltAccount Plugin $VERSION" \
    --notes-file "$RELEASE_NOTES_FILE" \
    "$JAR_FILE#alt-account-plugin-$VERSION.jar"; then
    
    log_success "Release $TAG créée avec succès!"
    log_success "JAR ajouté aux assets: $JAR_FILE"
    log_info "URL de la release: https://github.com/$(gh repo view --json owner,name -q '.owner.login + "/" + .name')/releases/tag/$TAG"
    
else
    log_error "Échec de la création de la release"
    rm -f "$RELEASE_NOTES_FILE"
    exit 1
fi

# Nettoyer le fichier temporaire
rm -f "$RELEASE_NOTES_FILE"

log_success "Processus terminé avec succès!"
echo ""
log_info "Les utilisateurs peuvent maintenant télécharger le JAR depuis:"
log_info "https://github.com/$(gh repo view --json owner,name -q '.owner.login + "/" + .name')/releases/latest"