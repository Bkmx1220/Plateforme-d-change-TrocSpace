<template>
  <div class="verification-container">
    <h2>Vérification de votre email</h2>

    <!-- Message d'erreur en cas de problème -->
    <p v-if="error" class="error">{{ error }}</p>

    <!-- Message de succès -->
    <p v-if="success" class="success">{{ success }}</p>

    <!-- Bouton de connexion si la vérification est réussie -->
    <div v-if="success">
      <p>Vous pouvez maintenant vous connecter.</p>
      <button @click="goToLoginPage">Se connecter</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      error: null,
      success: null,
    };
  },
  async mounted() {
    try {
      // Afficher un message pour vérifier si le composant est monté
      console.log('Composant monté');

      // Récupérer le token depuis l'URL
      const token = this.$route.query.token;

      // Vérifier si le token est manquant
      if (!token) {
        this.error = "Token manquant dans l'URL.";
        console.log("Token manquant");
        return; // Ne pas poursuivre l'exécution si le token est manquant
      }

      console.log("Token récupéré:", token); // Affichage du token dans la console

      // Faire la requête à l'API de vérification
      const response = await this.$axios.get(`http://localhost:8080/api/auth/verify?token=${encodeURIComponent(token)}`);

      console.log("Requête envoyée à l'API:", response.config); // Affiche la requête envoyée

      // Vérifier si la réponse du serveur est "Email vérifié avec succès"
      if (response.data === "Email vérifié avec succès") {
        this.success = "Email vérifié avec succès.";
      } else {
        this.error = response.data || 'Échec de la vérification de l\'email';
      }
    } catch (error) {
      console.error('Erreur lors de la vérification du token :', error);
      this.error = 'Une erreur est survenue lors de la vérification de votre email.';
    }
  },
  methods: {
    // Méthode pour rediriger vers la page de connexion
    goToLoginPage() {
      this.$router.push({name: 'Login'}); // Assurez-vous que la route 'Login' existe
    }
  }
};
</script>

<style scoped>
.verification-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.error {
  color: red;
  margin-top: 10px;
}

.success {
  color: green;
  margin-top: 10px;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
