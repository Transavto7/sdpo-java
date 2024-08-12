<script>
import PrintLogin from "@/pages/print/PrintLogin";
import PrintIndex from "@/pages/print/PrintIndex";
import PrintSelection from "@/pages/print/PrintSelection";
import Loader from "@/components/common/Loader";
import {getInspections, printInspection} from '@/helpers/api';
import {useToast} from "vue-toastification";
import {printQr} from "@/helpers/printer";


export default {
  components: {PrintSelection, Loader, PrintIndex, PrintLogin},
  data() {
    return {
      driver: null,
      toast: useToast(),
      inspections: {},
      print: false,
      errorAuthentication: false,
      loading: false,
      tab: 'all',
    }
  },
  methods: {
    async confirmPrint(inspection) {
      await printInspection(inspection)
      this.toast.success('Задание на печать отправлено');
      this.$router.push('/');

    },
    printQuery(inspectionAttr) {
      this.confirmPrint(inspectionAttr)
    },
    async setDriver(driver) {
      this.loading = true
      this.inspections = await getInspections(driver.hash_id);
      this.driver = driver;
      this.loading = false;

    },
    selectTab(type) {
      if (type === 'inspection'){
        this.tab = 'inspection';
        return ;
      }
      printQr(this.driver.hash_id, type);
      this.toast.success('Задание на печать отправлено');
    }
  },
  computed: {
    login() {
      return this.driver !== null
    },
    stepInspection() {
      return this.tab === 'inspection';
    }
  },
}
</script>

<template>
  <print-login v-if="!this.login && !this.loading" @success-auth="(driver) => setDriver(driver)"/>
  <print-selection v-if="this.login && !this.loading  && !this.stepInspection"
                  @select="(type) => selectTab(type)"/>
  <print-index v-if="this.login && !this.loading && this.stepInspection"
               @print="(inspectionAttr) => printQuery(inspectionAttr)"
               v-model:inspections="inspections"
               v-model:driver="driver"/>
  <loader v-model:loading="loading"/>
</template>
