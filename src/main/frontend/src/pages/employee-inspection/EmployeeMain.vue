<script>
import {useToast} from "vue-toastification";
import EmployeeLogin from "@/pages/employee-inspection/EmployeeLogin.vue";
import EmployeeIndex from "@/pages/employee-inspection/EmployeeIndex.vue";


export default {
  components: {EmployeeLogin, EmployeeIndex},
  data() {
    return {
      employee: null,
      toast: useToast(),
    }
  },
  methods: {
  },
  computed: {
    login() {
      return this.employee !== null
    },
  },
}
</script>

<template>
  <employee-login/>

</template>
