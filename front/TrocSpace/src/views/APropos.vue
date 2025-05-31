<template>
  <Navbar />
  <div class="apropos-container">
    <h1>À propos de TrocSpace</h1>
    <p>Bienvenue sur TrocSpace, une plateforme dédiée à l'échange d'objets, de services et bien plus encore.</p>
    <p>Notre mission est de rendre les échanges entre particuliers plus simples et plus accessibles.</p>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";

export default {
  name: 'APropos',
  components: {Navbar},
};
</script>

<style scoped>
.apropos-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

h1 {
  margin-bottom: 20px;
}
</style>
