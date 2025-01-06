<script>
import {getMedics, saveMedic} from '@/helpers/api/api';
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";

export default {
  components: {InputPersonalNumberForm},

  data() {
    return {
      medics: {},
      towns: [],
      status: "login",
      password: '',
    }
  },
  async mounted() {
    this.medics = await getMedics();
    this.towns = Object.keys(this.medics).sort();
  },
  methods: {
    updatePassword(inputPassword) {
        this.password = inputPassword;
    },
    login() {
      if (this.password !== this.config?.medic_password) {
        return;
      }

      this.password = '';
      this.status = 'select';
    },
    async select(user) {
      this.$store.state.config.main.selected_medic = {
        name: user.name,
        id: user.id,
        eds: user.eds,
        validity_eds_start: user.validity_eds_start ?? null,
        validity_eds_end: user.validity_eds_end ?? null,
      }

      this.status = 'login';
      await saveMedic(this.medic);
    }
  },
  computed: {
    medic() {
      return this.$store.state.config?.main?.selected_medic || {};
    },
    config() {
      return this.$store.state.config?.main;
    },
    visible() {
      return this.$store.state.selectingMedic;
    }
  },
  watch: {
    password: function (val) {
      this.login();
    }
  },
}
</script>

<template>
  <div v-if="visible"
       @click="$store.state.selectingMedic = false"
       class="medics__overlay animate__animated animate__fadeIn">
  </div>

  <div v-if="visible" class="medics animate__animated animate__fadeInRight">
    <div v-if="status === 'login'" class="medics__form">
      <div class="login-form__title">
        Введите пароль
      </div>

      <div class="login-form__input">
        <input type="password" v-model="password"/>
      </div>

      <input-personal-number-form
          @password=" (inputPassword) => updatePassword(inputPassword)"
      />

      <div class="medics__form-selected">
        {{ medic.name || 'Сотрудник не выбран' }}
      </div>
    </div>

    <div v-else class="medics__select">
      <div v-for="(name, index) in towns" :key="index">
        <div v-if="name" class="medics__town">
          {{ name }}
        </div>
        <div v-if="name" v-for="(point, name) in medics[name]" :key="name">
          <div class="medics__point">
            {{ name }}
          </div>

          <button v-for="user in point" :key="user.id" class="medics__item" @click="select(user)">
            {{ user.name }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>