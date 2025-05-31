<template>
  <!-- Navbar globalement visible sur toutes les pages -->
  <Navbar />
  <div class="contact-container">
    <h1>Contactez-nous</h1>
    <p>Si vous avez des questions, n'hésitez pas à nous contacter en remplissant le formulaire ci-dessous.</p>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="name">Nom</label>
        <input
            type="text"
            id="name"
            v-model="name"
            placeholder="Votre nom"
            required
        />
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input
            type="email"
            id="email"
            v-model="email"
            placeholder="Votre email"
            required
        />
      </div>
      <div class="form-group">
        <label for="message">Message</label>
        <textarea
            id="message"
            v-model="message"
            placeholder="Votre message"
            required
        ></textarea>
      </div>
      <button type="submit" class="btn btn-primary">Envoyer</button>
    </form>

    <div v-if="submitted" class="confirmation-message">
      <p>Merci pour votre message. Nous vous répondrons dans les plus brefs délais.</p>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";

export default {
  components: {Navbar},
  data() {
    return {
      name: '',
      email: '',
      message: '',
      submitted: false,
    };
  },
  methods: {
    handleSubmit() {
      // Simuler l'envoi du message (remplace par une vraie API si nécessaire)
      this.submitted = true;
      // Réinitialisation du formulaire
      this.name = '';
      this.email = '';
      this.message = '';
    },
  },
};
</script>

<style scoped>
.contact-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 15px;
}

label {
  font-weight: bold;
}

input,
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.confirmation-message {
  margin-top: 20px;
  background-color: #d4edda;
  padding: 10px;
  border: 1px solid #c3e6cb;
  border-radius: 5px;
}
</style>
