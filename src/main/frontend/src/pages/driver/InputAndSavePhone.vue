<script>
import InputPhoneNumber from "@/components/InputPhoneNumber";
import Loader from "@/components/common/Loader";
import {savePhone} from '@/helpers/api';

import {useToast} from "vue-toastification";


export default {
  name: "InputAndSavePhone",
  components: {Loader, InputPhoneNumber},
  data() {
    return {
      phoneNumber: '',
      toast: useToast(),
      loading: false,
    }
  },
  methods: {
    setPhoneNumber(phoneNumber) {
      this.phoneNumber = phoneNumber;
    },
    formattingPhoneNumber() {
      if (this.phoneNumber[0] === '+')
        return this.phoneNumber.slice(1)
      return this.phoneNumber
    },
    async storePhone() {
      this.loading = true
      await savePhone(this.formattingPhoneNumber(), this.$store.state.inspection.driver_id)
      this.loading = false
      this.$router.push('/')
    }
  },
  computed: {
    hasPhone() {
      return this.phoneNumber.length === 12;
    }
  }
}
</script>
<template>
  <loader v-model:loading="loading"/>
  <div v-if="!this.loading" class="input-save-phone">
    <input-phone-number
        @acceptOn="(phoneNumber) => setPhoneNumber(phoneNumber)"/>
    <button v-if="hasPhone" @click="storePhone()" class="btn animate__animated animate__fadeInUp">
      Сохранить
    </button>
  </div>
</template>

<style scoped>

</style>