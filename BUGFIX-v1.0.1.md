# 🔧 Correctif v1.0.1 - Erreur de Désérialisation

## 🐛 Problème résolu

**Erreur** : `ClassCastException: class java.util.ArrayList cannot be cast to class [Lorg.bukkit.inventory.ItemStack;`

**Cause** : Bukkit sauvegarde les tableaux ItemStack[] en tant qu'ArrayList dans les fichiers YAML, mais le code tentait de les caster directement en tableaux.

## ✅ Solution implémentée

### Modifications dans `AltData.java` :

1. **Conversion automatique ArrayList → ItemStack[]**
   ```java
   // Avant (cassait)
   data.inventory = (ItemStack[]) args.get("inventory");
   
   // Après (fonctionne)
   Object inventoryObj = args.get("inventory");
   if (inventoryObj instanceof List) {
       List<?> inventoryList = (List<?>) inventoryObj;
       data.inventory = inventoryList.toArray(new ItemStack[0]);
   }
   ```

2. **Protection contre les valeurs null**
   ```java
   // Valeurs par défaut sécurisées
   data.experience = exp != null ? (Integer) exp : 0;
   data.foodLevel = food != null ? (Integer) food : 20;
   data.health = hp != null ? ((Number) hp).doubleValue() : 20.0;
   ```

3. **Gestion robuste des types**
   - Support à la fois des List et des Array
   - Conversion automatique selon le type détecté
   - Valeurs par défaut cohérentes

## 🚀 Résultat

- ✅ Plus de crash lors du `/alt`
- ✅ Chargement correct des données existantes
- ✅ Compatibilité avec les anciens fichiers
- ✅ Stabilité renforcée du plugin

## 📦 Installation du correctif

1. **Remplacez** l'ancien JAR par `alt-account-plugin-1.0.1.jar`
2. **Redémarrez** le serveur
3. **Testez** avec `/alt MonAlt` - plus d'erreur !

## 🔍 Test de validation

```
/alt TestAlt
> Doit fonctionner sans erreur
> Inventaire vide créé
> Position sauvegardée

/main
> Retour au compte principal
> Données restaurées correctement
```

---

**Note** : Ce correctif maintient la compatibilité ascendante. Tous vos alts existants continueront de fonctionner normalement.