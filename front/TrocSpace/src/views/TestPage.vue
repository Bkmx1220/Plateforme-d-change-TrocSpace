<template>
  <div>
    <!-- Navbar -->
    <Navbar />

    <!-- Conteneur principal -->
    <div class="container mt-4">
      <div class="row main-row">
        <!-- Sidebar -->
        <div class="col-md-3 pe-4">
          <Sidebar />
        </div>

        <!-- Contenu principal -->
        <div class="col-md-9">
          <div class="content">
            <h2>Bienvenue sur la page de test</h2>
            <p>
              Cette section est l'espace du contenu principal. Vous pouvez y ajouter
              du texte, des images, des formulaires, ou tout autre élément que vous souhaitez afficher ici.
            </p>
            <p>
              Le but de cette page est de tester le layout avec une sidebar à gauche et du contenu à droite.
              L'espacement entre les deux sections devrait être visible grâce à l'utilisation de Flexbox.
            </p>
            <p>
              Vous pouvez également modifier le contenu de cette section pour ajouter plus d'éléments comme
              des cartes, des tableaux, etc.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";
import Sidebar from "@/components/Sidebar.vue";

export default {
  components: {
    Navbar,
    Sidebar,
  },
};
</script>

<style scoped>
/* Conteneur principal */
.container {
  padding: 20px;
}

/* Flexbox pour la ligne */
.main-row {
  display: flex;
  gap: 20px; /* Espacement entre la sidebar et le contenu principal */
}

/* Sidebar */
.col-md-3 {
  padding-right: 0;
}

/* Contenu principal */
.col-md-9 {
  padding-left: 0;
}

/* Ajustements visuels */
.row {
  display: flex;
  gap: 20px; /* Ajout d'un espacement entre les deux colonnes */
}

/* Contenu de la partie droite */
.content {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #333;
}

p {
  color: #555;
  line-height: 1.6;
}
</style>
