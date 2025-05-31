<template>
  <div class="body-background">
    <div class="form-container">
      <!-- Formulaire de connexion -->
      <form class="login-form" @submit.prevent="handleLogin">
        <h2>Connexion</h2>

        <!-- Bouton Google avec SVG -->
        <div class="social-buttons">
          <button type="button" class="social-button google">
            <svg
                xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                viewBox="0 0 48 48"
                class="icon google-icon"
            >
              <defs>
                <path
                    id="a"
                    d="M44.5 20H24v8.5h11.8C33.7 34.4 29.4 37 24 37c-7 0-13-5.7-13-13s6-13 13-13c3 0 5.7 1 7.9 2.8l6.4-6.4C34.8 4.8 29.6 3 24 3 11.8 3 2 12.8 2 25s9.8 22 22 22c10.8 0 21-8 21-22 0-1.3-.2-2.7-.5-4z"
                />
              </defs>
              <clipPath id="b">
                <use xlink:href="#a" overflow="visible" />
              </clipPath>
              <g clip-path="url(#b)">
                <path fill="#FBBC05" d="M0 37V11l17 13z" />
                <path fill="#EA4335" d="M0 11l17 13 7-6.1L48 14V0H0z" />
                <path fill="#34A853" d="M0 37l30-23 7.9.98L48 0v48H0z" />
                <path fill="#4285F4" d="M48 48L17 24l-4-3 35-10z" />
              </g>
            </svg>
            Continuer avec Google
          </button>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label for="email">Email</label>
          <input
              type="email"
              id="email"
              v-model="email"
              placeholder="Votre email"
              required
              autocomplete="email"
          />
        </div>

        <!-- Mot de passe -->
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
              type="password"
              id="password"
              v-model="password"
              placeholder="Votre mot de passe"
              required
              autocomplete="current-password"
          />
        </div>

        <!-- Bouton soumettre -->
        <button type="submit" class="submit-button btn btn-primary" :disabled="!isFormValid">
          Se connecter
        </button>

        <!-- Lien mot de passe oublié -->
        <p class="text-center mt-2">
          <a href="#" @click.prevent="handleForgotPassword">Mot de passe oublié ?</a>
        </p>

        <!-- Lien pour s'inscrire si l'on n'a pas de compte -->
        <p class="text-center mt-3">
          Pas encore de compte ? <router-link to="/register">S'inscrire</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      email: '',
      password: '',
    };
  },
  computed: {
    isFormValid() {
      return this.email && this.password;
    },
  },
  methods: {
    handleLogin() {
      const formData = {
        email: this.email,
        password: this.password,
      };

      axios
          .post('http://localhost:8080/api/auth/login', formData)
          .then((response) => {
            if (response.data.token) {
              const token = response.data.token;
              localStorage.setItem('auth_token', token);
              alert('Connexion réussie !');
              this.$router.push({name: 'Profile'});
            } else {
              alert('Token non trouvé dans la réponse.');
            }
          })
          .catch((error) => {
            console.error('Erreur de connexion :', error.response.data);
            alert('Identifiants invalides, veuillez réessayer.');
          });
    },
    handleForgotPassword() {
      if (!this.email) {
        alert('Veuillez entrer votre email pour continuer.');
        return;
      }

      axios
          .post(`http://localhost:8080/api/auth/forgot-password?email=${encodeURIComponent(this.email)}`)
          .then(() => {
            alert('Un email de réinitialisation a été envoyé.');
            this.$router.push({
              name: 'EmailSent',
              query: {email: this.email, motif: 'reset_password'},
            });
          })
          .catch((error) => {
            console.error('Erreur d\'envoi de l\'email :', error.response.data);
            alert('Une erreur est survenue. Veuillez vérifier votre email.');
          });
    },
  },
};
</script>

<style scoped>
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css";

.form-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  font-size: 14px;
  margin-bottom: 5px;
  color: #555;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
}

.submit-button {
  padding: 10px;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}
</style>
