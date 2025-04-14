<script>

import {useToast} from "vue-toastification";
import {getInspectionFromLocalStorage, sendInspectionToCrm} from "@/helpers/api/inspection/inspections";
import {saveAutoSendToCrmFlag} from "@/helpers/settings";

export default {
  name: "QueueToSend",
  async mounted() {
    this.inspections = await getInspectionFromLocalStorage();
  },
  data() {
    return {
      toast: useToast(),
      autoSave: this.$store.state.config?.system.auto_send_to_crm || false,
      inspections: [
      ],
    }
  },
  methods: {
    async changeSendStatus() {
      this.system.auto_send_to_crm = !this.autoSave;
      await saveAutoSendToCrmFlag(this.system.auto_send_to_crm);
    },
    async sendToCrm(inspection, index) {
      let success = await sendInspectionToCrm(inspection);
      console.log(success)
      if (success) this.inspections.splice(index, 1);

    }
  },
  computed: {
    system() {
      return this.$store.state.config?.system || {};
    },
  }
}
</script>

<template>
  <div>
    <div>
      <div class="admin__system-card__item" style="justify-content: center">
        <h2>Автоматическая отправка на сервер</h2>
        <label class="switch">
          <input type="checkbox" @click="changeSendStatus()" v-model="autoSave">
          <div class="slider round"></div>
        </label>
      </div>
    </div>
    <div class="stamp animate__animated animate__fadeInUp">
      <div class="stamp__body">
        <div class="queue-to-send-box">
          <div class="queue-to-send-box__header">
            <h2>Очередь на отправку (Водители)</h2>
          </div>
          <div class="queue-to-send-box__content">
            <div class="queue-item"
                 v-for="(inspection, index) in this.inspections"
                 :class="{selected : false}">
              <p class="header">{{ inspection.driver_fio }}</p>
              <p class="">{{ inspection.driver_id }}</p>
              <p class="">{{ inspection.created_at }}</p>
              <div class="btn-control-group">
                <button @click="sendToCrm(inspection, index)" class="license btn opacity blue animate__animated animate__fadeInDown">
                  <span v-if="inspection.status_send === 'UNSENT'">
                    <i  class="ri-send-plane-fill"></i>
                  Отправить
                  </span>
                  <span v-else>
                       <i class="ri-reply-fill"></i>
                  Повторить
                  </span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>

</style>