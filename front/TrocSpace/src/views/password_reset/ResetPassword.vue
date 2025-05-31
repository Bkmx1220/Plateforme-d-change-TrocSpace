<template>
  <div class="reset-password-container">
    <h2>Réinitialisation du mot de passe</h2>
    <form @submit.prevent="handleResetPassword">
      <!-- Champ pour le nouveau mot de passe -->
      <div class="form-group">
        <label for="new-password">Nouveau mot de passe</label>
        <input
            type="password"
            id="new-password"
            v-model="newPassword"
            placeholder="Entrez votre nouveau mot de passe"
            required
        />
        <!-- Affichage des erreurs de validation du mot de passe -->
        <p v-if="newPassword && !isPasswordValid" class="error-message">
          Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre.
        </p>
      </div>

      <!-- Champ pour confirmer le mot de passe -->
      <div class="form-group">
        <label for="confirm-password">Confirmer le mot de passe</label>
        <input
            type="password"
            id="confirm-password"
            v-model="confirmPassword"
            placeholder="Confirmez votre mot de passe"
            required
        />
      </div>

      <!-- Affichage des erreurs -->
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <!-- Bouton pour soumettre -->
      <button
          type="submit"
          class="submit-button btn btn-primary"
          :disabled="!isFormValid"
      >
        Réinitialiser le mot de passe
      </button>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      newPassword: "",
      confirmPassword: "",
      token: this.$route.query.token || "", // Récupère le token depuis l'URL
      errorMessage: "",
    };
  },
  computed: {
    // Vérification si les mots de passe correspondent
    isFormValid() {
      return (
          this.newPassword &&
          this.confirmPassword &&
          this.newPassword === this.confirmPassword &&
          this.isPasswordValid
      );
    },
    // Validation du mot de passe avec regex : 8 caractères, 1 majuscule, 1 minuscule, 1 chiffre
    isPasswordValid() {
      const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[A-Z])(?=.*[a-z]).{8,}$/;
      return regex.test(this.newPassword);
    },
  },
  methods: {
    async handleResetPassword() {
      if (!this.token) {
        this.errorMessage = "Token de réinitialisation manquant.";
        return;
      }

      if (this.newPassword !== this.confirmPassword) {
        this.errorMessage = "Les mots de passe ne correspondent pas.";
        return;
      }

      try {
        const response = await axios.post(
            "http://localhost:8080/api/auth/reset-password",
            {
              token: this.token,
              newPassword: this.newPassword,
            }
        );

        if (response.status === 200) {
          alert("Mot de passe réinitialisé avec succès !");
          this.$router.push({ name: "Login" }); // Redirige vers la page de connexion
        } else {
          this.errorMessage = "Une erreur s'est produite. Veuillez réessayer.";
        }
      } catch (error) {
        console.error("Erreur lors de la réinitialisation :", error.response);

        // Vérification du message d'erreur spécifique du serveur
        if (error.response?.data?.message === "Le nouveau mot de passe ne peut pas être identique à l'ancien") {
          this.errorMessage = "Le nouveau mot de passe ne peut pas être identique à l'ancien.";
        } else {
          this.errorMessage =
              error.response?.data?.message ||
              "Impossible de réinitialiser le mot de passe.";
        }
      }
    },
  },
};
</script>

<style scoped>
.reset-password-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  font-size: 14px;
  margin-bottom: 5px;
  color: #333;
}

input {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
}

.submit-button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
