"use strict";(self["webpackChunksdpo_frontend"]=self["webpackChunksdpo_frontend"]||[]).push([[420],{2419:function(t,e,s){s.d(e,{NO:function(){return r},QL:function(){return o},TQ:function(){return n},af:function(){return i},o:function(){return c}});var a=s(1857);async function i(){return await axios.post("device/photo").then((({data:t})=>t)).catch(a.i)}async function n(){return await axios.get("device/video/size").then((({data:t})=>t)).catch(a.i)}async function r(t){return await axios.post("device/media",{driver_id:t}).then((({data:t})=>t)).catch(a.i)}async function o(t){return await axios.post("device/media/stop",{driver_id:t}).then((({data:t})=>t)).catch(a.i)}async function c(){return await axios.post("device/video/test").then((({data:t})=>t)).catch(a.i)}},6240:function(t,e,s){s.r(e),s.d(e,{default:function(){return O}});var a=s(3396),i=s(7139),n=s.p+"img/alco_guide.a6a48b74.png",r=s.p+"img/alco_guide_2.a5bbcd2d.png";const o={class:"step-alcometer__outer"},c={key:0,class:"step-alcometer"},d=(0,a._)("h3",{class:"animate__animated animate__fadeInDown"},"Проверка на алкоголь",-1),l={class:"step-alcometer__items"},h=(0,a._)("div",{class:"step-alcometer__item animate__animated animate__fadeInUp d-1"},[(0,a._)("span",null,"1"),(0,a._)("img",{style:{"padding-right":"20px"},width:"100",src:n}),(0,a._)("label",null,"Проверьте, что вставлена воронка")],-1),u=(0,a._)("div",{class:"step-alcometer__item animate__animated animate__fadeInUp d-2"},[(0,a._)("span",null,"2"),(0,a._)("img",{width:"100",src:r}),(0,a.Uk)(" Держите алкотестер на расстоянии 2-3 см от рта ")],-1),m={class:"step-alcometer__text animate__animated animate__fadeInUp d-2"},p=(0,a._)("br",null,null,-1),_=(0,a._)("br",null,null,-1),v=(0,a._)("br",null,null,-1),y=(0,a._)("br",null,null,-1),w=(0,a._)("br",null,null,-1),b={key:0},f={key:1,style:{width:"100%"}},k={key:0,class:"alert red"},g=(0,a._)("i",{class:"ri-alarm-warning-fill"},null,-1),I={key:1,class:"step-alcometer"},x=(0,a.uE)('<h3 class="animate__animated animate__fadeInDown">Количественное определение алкоголя</h3><div class="step-alcometer__items"><div class="step-alcometer__item animate__animated animate__fadeInUp d-1"><span style="min-height:30px;">3</span><img style="padding-right:20px;" width="100" src="'+n+'"> Снимите воронку , установите мундштук </div><div class="step-alcometer__text animate__animated animate__fadeInUp d-2"> Снимите мундштук-воронка<br><br> Установите индивидуальный мундштук<br><br> Дождитесь ГОТОВ на экране алкометра<br><br> Начните дуть с умеренной силой до окончания<br> звукового сигнала.<br><br> Снимите индивидуальный мундштук<br><br> Установите мундштук-воронка<br><br></div></div>',2),R=[x],C={class:"step-buttons"};function U(t,e,s,n,r,x){return(0,a.wg)(),(0,a.iD)("div",o,[r.showRetry?(0,a.kq)("",!0):((0,a.wg)(),(0,a.iD)("div",c,[d,(0,a._)("div",l,[h,u,(0,a._)("div",m,[(0,a.Uk)(" Дождитесь ГОТОВ на экране алкометра"),p,_,(0,a.Uk)(" Начните дуть с умеренной силой до окончания"),v,(0,a.Uk)(" звукового сигнала."),y,w,r.needRetry?(0,a.kq)("",!0):((0,a.wg)(),(0,a.iD)("p",b,"Дуйте "+(0,i.zw)(x.status),1)),r.needRetry?((0,a.wg)(),(0,a.iD)("p",f,"Результат: Положительный")):(0,a.kq)("",!0)])]),r.showRetry?(0,a.kq)("",!0):((0,a.wg)(),(0,a.iD)("p",k,[g,(0,a.Uk)(" НЕ ПРИКАСАТЬСЯ К АЛКОТЕСТЕРУ ГУБАМИ ")]))])),r.showRetry?((0,a.wg)(),(0,a.iD)("div",I,R)):(0,a.kq)("",!0),(0,a._)("div",C,[(0,a._)("button",{onClick:e[0]||(e[0]=t=>x.prevStep()),class:"btn opacity blue"},"Назад"),JSON.parse(x.system.alcometer_skip)?((0,a.wg)(),(0,a.iD)("button",{key:0,onClick:e[1]||(e[1]=t=>x.nextStep()),class:"btn"},"Продолжить")):(0,a.kq)("",!0)])])}s(7658);var q=s(6700),D=s(2419),S={data(){return{interval:null,seconds:5,needRetry:!1,showRetry:!1}},methods:{async saveWebCam(){if(JSON.parse(this.system.camera_photo)&&!this.inspection.photo||JSON.parse(this.system.camera_video)&&!this.inspection.video){const t=await(0,D.NO)(this.$store.state.inspection.driver_id);this.inspection.photo=t?.photo,this.inspection.video=t?.video}},async stopWebCam(){await(0,D.QL)(this.$store.state.inspection.driver_id),this.inspection.photo=null,this.inspection.video=null},async nextStep(){this.$router.push({name:"step-sleep"})},async prevStep(){this.$router.push({name:"step-thermometer"})},async retry(){this.needRetry=!0,setTimeout((()=>{this.showRetry=!0}),3e3),await this.stopWebCam(),await(0,q.bP)(),await(0,q._L)(),this.runCountdown(),await this.saveWebCam()},hasResult(t){return!(void 0===t||null===t||"next"===t)},checkRetry(t){return this.system.alcometer_fast&&this.system.alcometer_retry&&Number(t)>0&&!this.needRetry},runCountdown(){this.seconds=5,this.timerInterval=setInterval((()=>{this.seconds--,this.seconds<1&&clearInterval(this.timerInterval)}),1e3)}},async mounted(){await this.saveWebCam(),this.runCountdown(),this.requestInterval=setInterval((async()=>{const t=await(0,q.pe)();this.hasResult(t)&&(this.checkRetry(t)?await this.retry():(this.inspection.alcometer_result=Number(t)||0,this.inspection.alcometer_mode=this.system.alcometer_fast?"0":"1",this.nextStep()))}),1e3)},unmounted(){(0,q.qL)(this.system.alcometer_fast),clearInterval(this.requestInterval),clearInterval(this.timerInterval)},computed:{inspection(){return this.$store.state.inspection},system(){return this.$store.state.config?.system||{}},status(){return 5===this.seconds?"через "+this.seconds+" секунд":1===this.seconds?"через "+this.seconds+" секунду":this.seconds<1?" прямо сейчас!":"через "+this.seconds+" секунды"}}},N=s(89);const $=(0,N.Z)(S,[["render",U]]);var O=$}}]);
//# sourceMappingURL=420.1ebc60d4.js.map