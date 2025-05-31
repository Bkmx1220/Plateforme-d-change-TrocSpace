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

        <!-- Contenu principal, réduit à 7 colonnes -->
        <div class="col-md-7">
          <slot />
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
  padding: 1em;
}

/* Flexbox pour la ligne */
.main-row {
  display: flex;
  gap: 1em; /* Espacement entre la sidebar et le contenu principal */
  align-items: flex-start; /* Alignement des éléments au haut de la ligne */
  width: 100%; /* S'assurer que la ligne prend toute la largeur disponible */
}

/* Sidebar */
.col-md-3 {
  padding-right: 0;
  /* La sidebar occupe 3 colonnes */
}

/* Contenu principal, réduit à 7 colonnes */
.col-md-7 {
  padding-left: 0;
  /* Le contenu principal occupe 7 colonnes maintenant */
  display: flex;
  flex-direction: column;
  gap: 1em; /* Espacement entre les éléments dans le contenu principal */
}

/* Ajustements visuels */
.row {
  display: flex;
  gap: 1em; /* Espacement entre les colonnes */
}
</style>
