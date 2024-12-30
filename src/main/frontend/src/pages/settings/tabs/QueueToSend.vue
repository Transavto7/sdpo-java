<script>

import {useToast} from "vue-toastification";
import {getInspectionFromLocalStorage} from "@/helpers/api/inspection/inspections";

export default {
  name: "QueueToSend",
  async mounted() {
    this.inspections = await getInspectionFromLocalStorage();
  },
  data() {
    return {
      toast: useToast(),
      inspections: [
        /*{
          driver_fio: "Иванов Иван Ив анович",
          driver_id: '12333123',
          status_send: 'UNSENT',
          created_at: '2024-12-01 11:11:11'
        },*/
      ],
    }
  },
  methods: {},
  computed: {}
}
</script>

<template>
  <div>
    <div class="stamp animate__animated animate__fadeInUp">
      <div class="stamp__body">
        <div class="queue-to-send-box">
          <div class="queue-to-send-box__header">
            <h2>Очередь на отправку</h2>
          </div>
          <div class="queue-to-send-box__content">
            <div class="queue-item"
                 v-for="inspection in this.inspections"
                 :class="{selected : false}">
              <p class="header">{{ inspection.driver_fio }}</p>
              <p class="">{{ inspection.driver_id }}</p>
              <p class="">{{ inspection.created_at }}</p>
              <div class="btn-control-group">
                <button class="license btn opacity blue animate__animated animate__fadeInDown">
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