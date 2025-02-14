<script>
import store from "@/store";

export default {

  methods: {
    selectMedic() {
      this.$emit('selectMedic')
    },
    getHelp() {
      this.$emit('getHelp')
    },
    settings() {
      this.$emit('settings')
    },
    getLastInspection() {
      this.$emit('getLastInspection')
    },
    printQr() {
      this.$emit('print-qr')
    }
  },
  computed: {
    connection() {
      return store.state.connection

    }
  }
}
</script>

<template>
  <button @click="selectMedic()" class="btn opacity animate__animated animate__fadeInDown">Мед
    работник
  </button>
  <button @click="getHelp()" class="btn opacity animate__animated animate__fadeInDown d-2">Помощь
  </button>
  <button @click="getLastInspection()" class="btn opacity animate__animated animate__fadeInDown d-2">Повторная печать
  </button>
  <button v-if="connection" @click="printQr()" class="btn opacity animate__animated animate__fadeInDown d-2">QR
  </button>
  <button @click="settings()" class="btn icon animate__animated animate__fadeInDown d-3"
          style="padding: 0 1.5em 0 1.25em;">
    <i class="ri-tools-fill"></i>
  </button>
  <div class="animate__animated animate__fadeInDown d-3">
    <span :class="{ green_gradient_circle: connection, red_gradient_circle: !connection } " class="activity-circle"></span>
  </div>
</template>
