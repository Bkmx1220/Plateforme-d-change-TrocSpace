<template>
  <div class="body-background">
    <div class="form-container">
      <!-- Formulaire d'inscription -->
      <form class="register-form" @submit.prevent="handleSubmit">
        <h2>Inscription</h2>

        <!-- Bouton Google avec SVG -->
        <div class="social-buttons">
          <button type="button" class="social-button google">
            <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 48 48" class="icon google-icon">
              <defs>
                <path id="a" d="M44.5 20H24v8.5h11.8C33.7 34.4 29.4 37 24 37c-7 0-13-5.7-13-13s6-13 13-13c3 0 5.7 1 7.9 2.8l6.4-6.4C34.8 4.8 29.6 3 24 3 11.8 3 2 12.8 2 25s9.8 22 22 22c10.8 0 21-8 21-22 0-1.3-.2-2.7-.5-4z"/>
              </defs>
              <clipPath id="b">
                <use xlink:href="#a" overflow="visible"/>
              </clipPath>
              <g clip-path="url(#b)">
                <path fill="#FBBC05" d="M0 37V11l17 13z"/>
                <path fill="#EA4335" d="M0 11l17 13 7-6.1L48 14V0H0z"/>
                <path fill="#34A853" d="M0 37l30-23 7.9.98L48 0v48H0z"/>
                <path fill="#4285F4" d="M48 48L17 24l-4-3 35-10z"/>
              </g>
            </svg>
            Continuer avec Google
          </button>
        </div>

        <!-- Nom -->
        <div class="form-group">
          <label for="nom">Nom</label>
          <input type="text" id="nom" v-model="nom" placeholder="Votre nom" required />
        </div>

        <!-- Prénom -->
        <div class="form-group">
          <label for="prenom">Prénoms</label>
          <input type="text" id="prenom" v-model="prenom" placeholder="Vos prénoms" required />
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

        <!-- Téléphone -->
        <div class="form-group">
          <label for="telephone">Téléphone</label>
          <div class="phone-input">
            <span class="prefix">+228</span>
            <input
                type="text"
                id="telephone"
                v-model="rawTelephone"
                @input="sanitizePhone"
                placeholder="XX XX XX XX"
                maxlength="8"
                required
            />
          </div>
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
              autocomplete="new-password"
              @input="validatePassword"
          />
          <!-- Message d'erreur si le mot de passe est invalide -->
          <p v-if="password && !isPasswordValid" class="error-message">
            Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre.
          </p>
        </div>


        <!-- Confirmation du mot de passe -->
        <div class="form-group">
          <label for="passwordConfirmation">Confirmez le mot de passe</label>
          <input
              type="password"
              id="passwordConfirmation"
              v-model="passwordConfirmation"
              placeholder="Confirmez votre mot de passe"
              required
              autocomplete="new-password"
          />
          <span v-if="!passwordsMatch && passwordConfirmation" class="error">
            Les mots de passe ne correspondent pas.
          </span>
        </div>

        <!-- Bouton soumettre -->
        <button type="submit" class="submit-button" :disabled="!isFormValid">
          S'inscrire
        </button>

        <!-- Lien pour se connecter si l'on a déjà un compte -->
        <p class="text-center mt-3">
          Déjà un compte ? <router-link to="/login">Se connecter</router-link>
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
      nom: '',
      prenom: '',
      email: '',
      rawTelephone: '',
      password: '',
      passwordConfirmation: '',
      isPasswordValid: false,  // pour vérifier si le mot de passe est valide
    };
  },
  computed: {
    passwordsMatch() {
      return this.password === this.passwordConfirmation;
    },
    isFormValid() {
      return (
          this.nom &&
          this.prenom &&
          this.email &&
          this.rawTelephone.length === 8 &&
          this.password &&
          this.passwordConfirmation &&
          this.passwordsMatch &&
          this.isPasswordValid
      );
    },
  },
  methods: {
    sanitizePhone(event) {
      const input = event.target.value.replace(/[^0-9]/g, '');
      this.rawTelephone = input.slice(0, 8); // Limiter le téléphone à 8 chiffres
    },
    validatePassword() {
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
      this.isPasswordValid = passwordRegex.test(this.password);
    },
    handleSubmit() {
      if (this.isFormValid) {
        const formData = {
          prenom: this.prenom,
          nom: this.nom,
          email: this.email,
          rawTelephone: this.rawTelephone,
          password: this.password,
          passwordConfirmation: this.passwordConfirmation,
        };

        axios
            .post('http://localhost:8080/api/auth/register', formData)
            .then(() => {
              this.$router.push({
                name: 'EmailSent',
                query: {
                  email: this.email,
                  motif: "confirmation"
                }
              });
            })
            .catch((error) => {
              console.error('Erreur lors de l\'inscription :', error.response.data);
              alert('Une erreur est survenue, veuillez réessayer.');
            });
      } else {
        alert('Veuillez remplir tous les champs correctement.');
      }
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


.register-form {
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

.error {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}

.phone-input {
  display: flex;
  align-items: center;
  gap: 5px;
}

.prefix {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  background-color: #f0f0f0;
  padding: 0.5em;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.submit-button {
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
  background-color: #aaa;
  cursor: not-allowed;
}

</style>
