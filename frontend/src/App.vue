<template>
  <div>
    <header>
      <div class="bar">
        <div class="brand">
          <svg viewBox="0 0 32 34" fill="none" stroke="#fff" stroke-width="3.1" stroke-linecap="round" stroke-linejoin="round">
            <path d="M16 5 C 11 10 11 17 16 20 C 21 23 21 28 18 31"/>
            <path d="M16 5 C 21 10 21 17 16 20 C 11 23 11 28 14 31"/>
          </svg>
          <div>
            <div class="brand-name">Aurora</div>
            <div class="brand-tag">{{ t('tagline') }}</div>
          </div>
        </div>
        <nav>
          <button :class="{ active: activeTab === 'dashboard' }" @click="switchTab('dashboard')">{{ t('nav_dashboard') }}</button>
          <button :class="{ active: activeTab === 'patients' }" @click="switchTab('patients')">{{ t('nav_patients') }}</button>
          <button :class="{ active: activeTab === 'diagnose' }" @click="switchTab('diagnose')">{{ t('nav_diagnose') }}</button>
          <button :class="{ active: activeTab === 'records' }" @click="switchTab('records')">{{ t('nav_records') }}</button>
        </nav>
        <div class="lang">
          <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2">
            <circle cx="12" cy="12" r="9"/>
            <path d="M3 12h18M12 3c2.5 2.7 2.5 15.3 0 18M12 3c-2.5 2.7-2.5 15.3 0 18"/>
          </svg>
          <select v-model="currentLang">
            <option value="en">English</option>
            <option value="zh">中文</option>
            <option value="ru">Русский</option>
            <option value="uz">Oʻzbek</option>
            <option value="kk">Қазақша</option>
            <option value="tr">Türkçe</option>
          </select>
        </div>
      </div>
    </header>

    <main>
      <section :class="{ active: activeTab === 'dashboard' }">
        <div class="head">
          <h2>{{ t('dash_title') }}</h2>
          <p>{{ t('dash_sub') }}</p>
        </div>
        <div class="stats">
          <div class="stat card">
            <div class="stat-header">
              <div class="label">{{ t('stat_patients') }}</div>
              <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            </div>
            <div class="value">{{ statsData.totalPatients ?? '–' }}</div>
          </div>
          <div class="stat card">
            <div class="stat-header">
              <div class="label">{{ t('stat_diagnoses') }}</div>
              <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
            </div>
            <div class="value">{{ statsData.totalDiagnoses ?? '–' }}</div>
          </div>
          <div class="stat card mal">
            <div class="stat-header">
              <div class="label">{{ t('stat_malignant') }}</div>
              <svg class="stat-icon mal-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
            </div>
            <div class="value">{{ statsData.malignant ?? '–' }}</div>
          </div>
          <div class="stat card ben">
            <div class="stat-header">
              <div class="label">{{ t('stat_benign') }}</div>
              <svg class="stat-icon ben-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            </div>
            <div class="value">{{ statsData.benign ?? '–' }}</div>
          </div>
        </div>
        <div class="card pad" style="max-width: 800px;">
          <h3 style="margin-bottom:14px">{{ t('dash_subtypes') }}</h3>
          <div class="bars" v-if="Object.keys(statsData.subtypeDistribution || {}).length">
            <div class="barrow" v-for="(v, k) in statsData.subtypeDistribution" :key="k">
              <span>{{ pretty(k) }}</span>
              <div class="track">
                <div class="fill" :style="{ width: (v / maxSubtype) * 100 + '%' }"></div>
              </div>
              <span class="mono">{{ v }}</span>
            </div>
            <div class="axis">
                <span></span> <div class="axis-ticks">
                    <span>0</span>
                    <span>25%</span>
                    <span>50%</span>
                    <span>75%</span>
                    <span>100%</span>
                </div>
                <span></span> </div>
          </div>
          <p v-else style="color:var(--muted)">{{ t('dash_none') }}</p>
        </div>
      </section>

      <section :class="{ active: activeTab === 'patients' }">
        <div class="head">
          <h2>{{ t('pat_title') }}</h2>
          <p>{{ t('pat_sub') }}</p>
        </div>
        <div class="toolbar" style="max-width: 800px;">
          <input class="search" v-model="patientSearch" :placeholder="t('pat_search')">
          <button class="btn" @click="loadPatients(0)">{{ t('btn_search') }}</button>
        </div>
        
        <div class="card pad" style="margin-bottom:22px; max-width: 800px;">
          <h3 style="margin-bottom:14px">{{ t('pat_register') }}</h3>
          <div v-if="pMsg.text" class="msg" :class="pMsg.ok ? 'ok' : 'err'">{{ pMsg.text }}</div>
          <div class="grid2">
            <div>
              <label class="f">{{ t('f_pid') }}</label>
              <input v-model="pForm.pid" placeholder="P-002">
            </div>
            <div>
              <label class="f">{{ t('f_name') }}</label>
              <input v-model="pForm.name">
            </div>
            <div>
              <label class="f">{{ t('f_gender') }}</label>
              <select class="in" v-model="pForm.gender">
                <option value="FEMALE">{{ t('g_female') }}</option>
                <option value="MALE">{{ t('g_male') }}</option>
                <option value="OTHER">{{ t('g_other') }}</option>
              </select>
            </div>
            <div>
              <label class="f">{{ t('f_pass') }}</label>
              <input v-model="pForm.pass">
            </div>
          </div>
          <button class="btn" style="margin-top:16px" @click="addPatient">{{ t('btn_add') }}</button>
        </div>
        
        <div class="card">
          <table>
            <thead>
              <tr>
                <th>{{ t('th_pid') }}</th>
                <th>{{ t('th_name') }}</th>
                <th>{{ t('th_gender') }}</th>
                <th>{{ t('th_pass') }}</th>
                <th style="text-align:right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in patients" :key="p.id">
                <td class="mono">{{ p.patientId }}</td>
                <td>{{ p.fullName }}</td>
                <td>{{ p.gender }}</td>
                <td class="mono">{{ p.passportNumber }}</td>
                <td style="text-align:right">
                  <div class="table-actions">
                    <button class="icon-btn" title="View Record">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                    </button>
                    <button class="icon-btn danger" title="Delete" @click="delPatient(p.id)">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="!patients.length">
                <td colspan="5" style="color:var(--muted)">{{ t('pat_none') }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="pager">
          <button class="btn ghost" :disabled="pPageObj.first" @click="loadPatients(pPage - 1)">{{ t('btn_prev') }}</button>
          <span>{{ pginfo(pPageObj) }}</span>
          <button class="btn ghost" :disabled="pPageObj.last" @click="loadPatients(pPage + 1)">{{ t('btn_next') }}</button>
        </div>
      </section>

      <section :class="{ active: activeTab === 'diagnose' }">
        <div class="head">
          <h2>{{ t('diag_title') }}</h2>
          <p>{{ t('diag_sub') }}</p>
        </div>
        <div class="card pad" style="max-width: 800px;">
          <div v-if="dMsg.text" class="msg" :class="dMsg.ok ? 'ok' : 'err'">{{ dMsg.text }}</div>
          <div class="grid2">
            <div>
              <label class="f">{{ t('d_patient') }}</label>
              <select class="in" v-model="dForm.patientId">
                <option v-for="p in patientOptions" :key="p.id" :value="p.id">
                  {{ p.patientId }} - {{ p.fullName }}
                </option>
              </select>
            </div>
            <div>
              <label class="f">{{ t('d_date') }}</label>
              <input type="date" v-model="dForm.date">
            </div>
          </div>
          
          <div style="margin-top:24px">
            <label class="f">{{ t('d_image') }}</label>
            <div class="drop-zone">
                <input type="file" id="d-file" @change="handleFileUpload" accept="image/*" class="file-hidden">
                <label for="d-file" class="drop-zone-content">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="drop-icon"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
                    <span class="drop-text" v-if="!dForm.imageFile">Click to browse or drag image here</span>
                    <span class="drop-text file-selected" v-else>{{ dForm.imageFile.name }}</span>
                </label>
            </div>
          </div>
          
          <button class="btn" :disabled="isAnalyzing" style="margin-top:20px" @click="runDiagnose">
            {{ isAnalyzing ? t('analyzing') : t('btn_analyze') }}
          </button>
          <p class="note">{{ t('diag_note') }}</p>
          
          <div v-if="diagResult" class="card pad result" :class="diagResult.binaryLabel === 'malignant' ? 'mal' : 'ben'">
            <div class="result-header">
                <h3>Diagnosis Output</h3>
                <span class="badge large" :class="diagResult.binaryLabel === 'malignant' ? 'mal' : 'ben'">
                    {{ diagResult.binaryLabel === 'malignant' ? t('badge_malignant') : t('badge_benign') }}
                </span>
            </div>
            
            <div class="kv">
              <div>
                <div class="k">{{ t('r_pmal') }}</div>
                <div class="v mono highlight-pct">{{ pct(diagResult.binaryProbability) }}</div>
              </div>
              <div>
                <div class="k">{{ t('r_subtype') }}</div>
                <div class="v">{{ pretty(diagResult.subtypeLabel) }}</div>
              </div>
              <div>
                <div class="k">{{ t('r_subconf') }}</div>
                <div class="v mono">{{ pct(diagResult.subtypeConfidence) }}</div>
              </div>
            </div>
            <p class="note">{{ t('saved_record') }} #{{ diagResult.id }} · {{ diagResult.imageFilename }} · {{ diagResult.testDate }}</p>
          </div>
        </div>
      </section>

      <section :class="{ active: activeTab === 'records' }">
        <div class="head">
          <h2>{{ t('rec_title') }}</h2>
          <p>{{ t('rec_sub') }}</p>
        </div>
        <div class="toolbar">
          <select class="in" v-model="rFilters.label">
            <option value="">{{ t('all_labels') }}</option>
            <option value="malignant">{{ t('opt_malignant') }}</option>
            <option value="benign">{{ t('opt_benign') }}</option>
          </select>
          <input type="date" v-model="rFilters.from">
          <input type="date" v-model="rFilters.to">
          <button class="btn" @click="loadRecords(0)">{{ t('btn_filter') }}</button>
        </div>
        
        <div class="card">
          <table>
            <thead>
              <tr>
                <th>{{ t('th_date') }}</th>
                <th>{{ t('th_patient') }}</th>
                <th>{{ t('th_result') }}</th>
                <th>P(mal)</th>
                <th>{{ t('th_subtype') }}</th>
                <th>{{ t('th_image') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in records" :key="r.id">
                <td class="mono">{{ r.testDate }}</td>
                <td>{{ r.patient ? r.patient.patientId : '–' }}</td>
                <td>
                  <span class="badge solid" :class="r.binaryLabel === 'malignant' ? 'mal' : 'ben'">
                    {{ r.binaryLabel === 'malignant' ? t('badge_malignant') : t('badge_benign') }}
                  </span>
                </td>
                <td class="mono">{{ pct(r.binaryProbability) }}</td>
                <td>{{ pretty(r.subtypeLabel) }}</td>
                <td class="mono" style="font-size:12px">
                    <div class="image-cell">
                        <svg class="img-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
                        <span>{{ r.imageFilename || 'image.png' }}</span>
                    </div>
                </td>
              </tr>
              <tr v-if="!records.length">
                <td colspan="6" style="color:var(--muted)">{{ rMsg.text || t('rec_none') }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="pager">
          <button class="btn ghost" :disabled="rPageObj.first" @click="loadRecords(rPage - 1)">{{ t('btn_prev') }}</button>
          <span>{{ pginfo(rPageObj) }}</span>
          <button class="btn ghost" :disabled="rPageObj.last" @click="loadRecords(rPage + 1)">{{ t('btn_next') }}</button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';

// --- I18N DICTIONARY ---
const I18N = {
  en:{tagline:"Early detection saves lives",nav_dashboard:"Dashboard",nav_patients:"Patients",nav_diagnose:"Diagnose",nav_records:"Records",dash_title:"Overview",dash_sub:"Live summary of patients and diagnostic activity.",stat_patients:"Patients",stat_diagnoses:"Diagnoses",stat_malignant:"Malignant",stat_benign:"Benign",dash_subtypes:"Subtype distribution",dash_none:"No diagnoses yet.",pat_title:"Patients",pat_sub:"Search, browse and register patients.",pat_search:"Search name, patient ID, or passport…",btn_search:"Search",pat_register:"Register patient",f_pid:"Patient ID",f_name:"Full name",f_gender:"Gender",g_female:"Female",g_male:"Male",g_other:"Other",f_pass:"Passport number",btn_add:"Add patient",th_pid:"Patient ID",th_name:"Name",th_gender:"Gender",th_pass:"Passport",btn_delete:"Delete",pat_none:"No patients found.",msg_registered:"Patient registered.",diag_title:"Run diagnosis",diag_sub:"Upload a histopathology tile; the model returns a benign/malignant call plus subtype.",d_patient:"Patient",d_date:"Test date",d_image:"Histopathology image",btn_analyze:"Analyze image",analyzing:"Analyzing…",diag_note:"Requires the analysis service to be running.",sel_patient:"Select a patient first.",choose_image:"Choose an image.",r_pmal:"P(malignant)",r_subtype:"Subtype",r_subconf:"Subtype confidence",saved_record:"Saved as record",diag_saved:"Diagnosis saved.",rec_title:"Diagnostic records",rec_sub:"All diagnoses across patients, filterable.",all_labels:"All labels",opt_malignant:"Malignant",opt_benign:"Benign",btn_filter:"Filter",th_date:"Date",th_patient:"Patient",th_result:"Result",th_subtype:"Subtype",th_image:"Image",rec_none:"No records.",badge_malignant:"Malignant",badge_benign:"Benign",btn_prev:"Prev",btn_next:"Next",pageinfo:"Page {p} of {tp} · {n} total"},
  zh:{tagline:"早期发现，挽救生命",nav_dashboard:"仪表板",nav_patients:"患者",nav_diagnose:"诊断",nav_records:"记录",dash_title:"概览",dash_sub:"患者与诊断活动实时摘要。",stat_patients:"患者",stat_diagnoses:"诊断",stat_malignant:"恶性",stat_benign:"良性",dash_subtypes:"亚型分布",dash_none:"暂无诊断。",pat_title:"患者",pat_sub:"搜索、浏览并登记患者。",pat_search:"搜索姓名、患者编号或护照…",btn_search:"搜索",pat_register:"登记患者",f_pid:"患者编号",f_name:"姓名",f_gender:"性别",g_female:"女",g_male:"男",g_other:"其他",f_pass:"护照号码",btn_add:"添加患者",th_pid:"患者编号",th_name:"姓名",th_gender:"性别",th_pass:"护照",btn_delete:"删除",pat_none:"未找到患者。",msg_registered:"患者已登记。",diag_title:"进行诊断",diag_sub:"上传组织病理图像；模型将返回良性/恶性判断及亚型。",d_patient:"患者",d_date:"检测日期",d_image:"组织病理图像",btn_analyze:"分析图像",analyzing:"分析中…",diag_note:"需要分析服务正在运行。",sel_patient:"请先选择患者。",choose_image:"请选择图像。",r_pmal:"恶性概率",r_subtype:"亚型",r_subconf:"亚型置信度",saved_record:"已保存为记录",diag_saved:"诊断已保存。",rec_title:"诊断记录",rec_sub:"所有患者的诊断，可筛选。",all_labels:"全部",opt_malignant:"恶性",opt_benign:"良性",btn_filter:"筛选",th_date:"日期",th_patient:"患者",th_result:"结果",th_subtype:"亚型",th_image:"图像",rec_none:"暂无记录。",badge_malignant:"恶性",badge_benign:"良性",btn_prev:"上一页",btn_next:"下一页",pageinfo:"第 {p}/{tp} 页 · 共 {n} 条"},
  ru:{tagline:"Раннее выявление спасает жизни",nav_dashboard:"Панель",nav_patients:"Пациенты",nav_diagnose:"Диагностика",nav_records:"Записи",dash_title:"Обзор",dash_sub:"Сводка по пациентам и диагностике в реальном времени.",stat_patients:"Пациенты",stat_diagnoses:"Диагнозы",stat_malignant:"Злокачественные",stat_benign:"Доброкачественные",dash_subtypes:"Распределение подтипов",dash_none:"Пока нет диагнозов.",pat_title:"Пациенты",pat_sub:"Поиск, просмотр и регистрация пациентов.",pat_search:"Поиск по имени, ID или паспорту…",btn_search:"Поиск",pat_register:"Регистрация пациента",f_pid:"ID пациента",f_name:"Полное имя",f_gender:"Пол",g_female:"Женский",g_male:"Мужской",g_other:"Другое",f_pass:"Номер паспорта",btn_add:"Добавить пациента",th_pid:"ID пациента",th_name:"Имя",th_gender:"Пол",th_pass:"Паспорт",btn_delete:"Удалить",pat_none:"Пациенты не найдены.",msg_registered:"Пациент зарегистрирован.",diag_title:"Провести диагностику",diag_sub:"Загрузите гистологический снимок; модель вернёт результат и подтип.",d_patient:"Пациент",d_date:"Дата теста",d_image:"Гистологическое изображение",btn_analyze:"Анализировать",analyzing:"Анализ…",diag_note:"Требуется запущенный сервис анализа.",sel_patient:"Сначала выберите пациента.",choose_image:"Выберите изображение.",r_pmal:"Вероятность злок.",r_subtype:"Подтип",r_subconf:"Достоверность подтипа",saved_record:"Сохранено как запись",diag_saved:"Диагноз сохранён.",rec_title:"Диагностические записи",rec_sub:"Все диагнозы по пациентам, с фильтрами.",all_labels:"Все",opt_malignant:"Злокачественные",opt_benign:"Доброкачественные",btn_filter:"Фильтр",th_date:"Дата",th_patient:"Пациент",th_result:"Результат",th_subtype:"Подтип",th_image:"Изображение",rec_none:"Нет записей.",badge_malignant:"Злокач.",badge_benign:"Доброкач.",btn_prev:"Назад",btn_next:"Вперёд",pageinfo:"Стр. {p} из {tp} · всего {n}"},
  uz:{tagline:"Erta aniqlash hayot saqlaydi",nav_dashboard:"Boshqaruv",nav_patients:"Bemorlar",nav_diagnose:"Tashxis",nav_records:"Yozuvlar",dash_title:"Umumiy koʻrinish",dash_sub:"Bemorlar va tashxis faoliyatining jonli xulosasi.",stat_patients:"Bemorlar",stat_diagnoses:"Tashxislar",stat_malignant:"Xavfli",stat_benign:"Xavfsiz",dash_subtypes:"Kichik turlar taqsimoti",dash_none:"Hozircha tashxis yoʻq.",pat_title:"Bemorlar",pat_sub:"Bemorlarni qidirish, koʻrish va roʻyxatga olish.",pat_search:"Ism, bemor ID yoki pasport boʻyicha qidirish…",btn_search:"Qidirish",pat_register:"Bemorni roʻyxatga olish",f_pid:"Bemor ID",f_name:"Toʻliq ism",f_gender:"Jinsi",g_female:"Ayol",g_male:"Erkak",g_other:"Boshqa",f_pass:"Pasport raqami",btn_add:"Bemor qoʻshish",th_pid:"Bemor ID",th_name:"Ism",th_gender:"Jinsi",th_pass:"Pasport",btn_delete:"Oʻchirish",pat_none:"Bemor topilmadi.",msg_registered:"Bemor roʻyxatga olindi.",diag_title:"Tashxis oʻtkazish",diag_sub:"Gistopatologik rasm yuklang; model natija va kichik turni qaytaradi.",d_patient:"Bemor",d_date:"Tekshiruv sanasi",d_image:"Gistopatologik rasm",btn_analyze:"Rasmni tahlil qilish",analyzing:"Tahlil qilinmoqda…",diag_note:"Tahlil xizmati ishlab turishi kerak.",sel_patient:"Avval bemorni tanlang.",choose_image:"Rasm tanlang.",r_pmal:"Xavfli ehtimoli",r_subtype:"Kichik tur",r_subconf:"Kichik tur ishonchi",saved_record:"Yozuv sifatida saqlandi",diag_saved:"Tashxis saqlandi.",rec_title:"Tashxis yozuvlari",rec_sub:"Barcha bemorlar tashxislari, filtrlanadi.",all_labels:"Barchasi",opt_malignant:"Xavfli",opt_benign:"Xavfsiz",btn_filter:"Filtr",th_date:"Sana",th_patient:"Bemor",th_result:"Natija",th_subtype:"Kichik tur",th_image:"Rasm",rec_none:"Yozuv yoʻq.",badge_malignant:"Xavfli",badge_benign:"Xavfsiz",btn_prev:"Orqaga",btn_next:"Oldinga",pageinfo:"{p}/{tp}-bet · jami {n}"},
  kk:{tagline:"Ерте анықтау өмірді сақтайды",nav_dashboard:"Басқару",nav_patients:"Науқастар",nav_diagnose:"Диагностика",nav_records:"Жазбалар",dash_title:"Шолу",dash_sub:"Науқастар мен диагностика әрекетінің нақты қорытындысы.",stat_patients:"Науқастар",stat_diagnoses:"Диагноздар",stat_malignant:"Қатерлі",stat_benign:"Қатерсіз",dash_subtypes:"Ішкі түрлердің таралуы",dash_none:"Әзірге диагноз жоқ.",pat_title:"Науқастар",pat_sub:"Науқастарды іздеу, қарау және тіркеу.",pat_search:"Аты, науқас ID немесе төлқұжат бойынша іздеу…",btn_search:"Іздеу",pat_register:"Науқасты тіркеу",f_pid:"Науқас ID",f_name:"Толық аты",f_gender:"Жынысы",g_female:"Әйел",g_male:"Ер",g_other:"Басқа",f_pass:"Төлқұжат нөмірі",btn_add:"Науқас қосу",th_pid:"Науқас ID",th_name:"Аты",th_gender:"Жынысы",th_pass:"Төлқұжат",btn_delete:"Жою",pat_none:"Науқас табылмады.",msg_registered:"Науқас тіркелді.",diag_title:"Диагноз қою",diag_sub:"Гистопатологиялық сурет жүктеңіз; модель нәтиже мен ішкі түрді қайтарады.",d_patient:"Науқас",d_date:"Тексеру күні",d_image:"Гистопатологиялық сурет",btn_analyze:"Суретті талдау",analyzing:"Талдануда…",diag_note:"Талдау қызметі іске қосылған болуы керек.",sel_patient:"Алдымен науқасты таңдаңыз.",choose_image:"Сурет таңдаңыз.",r_pmal:"Қатерлі ықтималдығы",r_subtype:"Ішкі түр",r_subconf:"Ішкі түр сенімі",saved_record:"Жазба ретінде сақталды",diag_saved:"Диагноз сақталды.",rec_title:"Диагностикалық жазбалар",rec_sub:"Барлық науқастар диагноздары, сүзгіленеді.",all_labels:"Барлығы",opt_malignant:"Қатерлі",opt_benign:"Қатерсіз",btn_filter:"Сүзу",th_date:"Күні",th_patient:"Науқас",th_result:"Нәтиже",th_subtype:"Ішкі түр",th_image:"Сурет",rec_none:"Жазба жоқ.",badge_malignant:"Қатерлі",badge_benign:"Қатерсіз",btn_prev:"Артқа",btn_next:"Алға",pageinfo:"{p}/{tp} бет · барлығы {n}"},
  tr:{tagline:"Erken teşhis hayat kurtarır",nav_dashboard:"Panel",nav_patients:"Hastalar",nav_diagnose:"Teşhis",nav_records:"Kayıtlar",dash_title:"Genel Bakış",dash_sub:"Hastaların ve teşhis etkinliğinin canlı özeti.",stat_patients:"Hastalar",stat_diagnoses:"Teşhisler",stat_malignant:"Kötü huylu",stat_benign:"İyi huylu",dash_subtypes:"Alt tür dağılımı",dash_none:"Henüz teşhis yok.",pat_title:"Hastalar",pat_sub:"Hasta ara, görüntüle ve kaydet.",pat_search:"Ad, hasta kimliği veya pasaport ara…",btn_search:"Ara",pat_register:"Hasta kaydı",f_pid:"Hasta Kimliği",f_name:"Ad Soyad",f_gender:"Cinsiyet",g_female:"Kadın",g_male:"Erkek",g_other:"Diğer",f_pass:"Pasaport numarası",btn_add:"Hasta ekle",th_pid:"Hasta Kimliği",th_name:"Ad",th_gender:"Cinsiyet",th_pass:"Pasaport",btn_delete:"Sil",pat_none:"Hasta bulunamadı.",msg_registered:"Hasta kaydedildi.",diag_title:"Teşhis yap",diag_sub:"Histopatoloji görüntüsü yükleyin; model sonucu ve alt türü döndürür.",d_patient:"Hasta",d_date:"Test tarihi",d_image:"Histopatoloji görüntüsü",btn_analyze:"Görüntüyü analiz et",analyzing:"Analiz ediliyor…",diag_note:"Analiz servisinin çalışıyor olması gerekir.",sel_patient:"Önce bir hasta seçin.",choose_image:"Bir görüntü seçin.",r_pmal:"Kötü huylu olasılığı",r_subtype:"Alt tür",r_subconf:"Alt tür güveni",saved_record:"Kayıt olarak saklandı",diag_saved:"Teşhis kaydedildi.",rec_title:"Teşhis kayıtları",rec_sub:"Tüm hastaların teşhisleri, filtrelenebilir.",all_labels:"Tümü",opt_malignant:"Kötü huylu",opt_benign:"İyi huylu",btn_filter:"Filtrele",th_date:"Tarih",th_patient:"Hasta",th_result:"Sonuç",th_subtype:"Alt tür",th_image:"Görüntü",rec_none:"Kayıt yok.",badge_malignant:"Kötü huylu",badge_benign:"İyi huylu",btn_prev:"Geri",btn_next:"İleri",pageinfo:"Sayfa {p}/{tp} · toplam {n}"}
};

// --- REACTIVE STATE ---
const activeTab = ref('dashboard');
const currentLang = ref('en');

// State objects
const statsData = ref({});
const patients = ref([]);
const patientOptions = ref([]);
const records = ref([]);

// Pagination State
const pPage = ref(0);
const pPageObj = reactive({ number: 0, totalPages: 1, totalElements: 0, first: true, last: true });
const rPage = ref(0);
const rPageObj = reactive({ number: 0, totalPages: 1, totalElements: 0, first: true, last: true });

// Form Models
const patientSearch = ref('');
const pForm = reactive({ pid: '', name: '', gender: 'FEMALE', pass: '' });
const dForm = reactive({ patientId: '', date: new Date().toISOString().slice(0, 10), imageFile: null });
const rFilters = reactive({ label: '', from: '', to: '' });

// Message & Status Handling
const pMsg = reactive({ text: '', ok: true });
const dMsg = reactive({ text: '', ok: true });
const rMsg = reactive({ text: '', ok: true });

const isAnalyzing = ref(false);
const diagResult = ref(null);

// --- COMPUTED & UTILS ---
const t = (k) => (I18N[currentLang.value] && I18N[currentLang.value][k]) || I18N.en[k] || k;
const maxSubtype = computed(() => Math.max(1, ...Object.values(statsData.value.subtypeDistribution || {})));

const pct = (x) => (x * 100).toFixed(1) + '%';
const pretty = (s) => s ? s.split('_').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ') : s;
const pginfo = (d) => t('pageinfo').replace('{p}', d.number + 1).replace('{tp}', d.totalPages || 1).replace('{n}', d.totalElements);

// --- CORE LOGIC ---
const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'dashboard') loadStats();
  if (tab === 'patients') loadPatients(pPage.value);
  if (tab === 'diagnose') loadPatientOptions();
  if (tab === 'records') loadRecords(rPage.value);
};

const apiCall = async (path, opts) => {
  const res = await fetch(path, opts);
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    const d = data && (data.detail || data.title) || ("HTTP " + res.status);
    const f = data && data.errors ? " (" + Object.values(data.errors).join(", ") + ")" : "";
    throw new Error(d + f);
  }
  return data;
};

// Dashboard
const loadStats = async () => {
  try {
    statsData.value = await apiCall("/api/stats");
  } catch (e) {
    console.error(e);
  }
};

// Patients
const loadPatients = async (page) => {
  if (page < 0) return;
  try {
    const d = await apiCall(`/api/patients?q=${encodeURIComponent(patientSearch.value.trim())}&page=${page}&size=8&sortBy=id`);
    patients.value = d.content || [];
    pPage.value = d.number;
    Object.assign(pPageObj, d);
  } catch (e) {
    pMsg.text = e.message; pMsg.ok = false;
  }
};

const addPatient = async () => {
  const body = { patientId: pForm.pid.trim(), fullName: pForm.name.trim(), gender: pForm.gender, passportNumber: pForm.pass.trim() };
  try {
    await apiCall("/api/patients", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
    pMsg.text = t('msg_registered'); pMsg.ok = true;
    pForm.pid = ''; pForm.name = ''; pForm.pass = ''; // reset form
    loadPatients(0);
  } catch (e) {
    pMsg.text = e.message; pMsg.ok = false;
  }
};

const delPatient = async (id) => {
  try {
    await apiCall("/api/patients/" + id, { method: "DELETE" });
    loadPatients(pPage.value);
  } catch (e) {
    pMsg.text = e.message; pMsg.ok = false;
  }
};

// Diagnose
const loadPatientOptions = async () => {
  try {
    const d = await apiCall("/api/patients?size=100&sortBy=id");
    patientOptions.value = d.content || [];
  } catch (e) {
    dMsg.text = e.message; dMsg.ok = false;
  }
};

const handleFileUpload = (e) => {
  dForm.imageFile = e.target.files[0];
};

const runDiagnose = async () => {
  if (!dForm.patientId) { dMsg.text = t('sel_patient'); dMsg.ok = false; return; }
  if (!dForm.imageFile) { dMsg.text = t('choose_image'); dMsg.ok = false; return; }
  
  const fd = new FormData(); 
  fd.append("image", dForm.imageFile); 
  if (dForm.date) fd.append("testDate", dForm.date);
  
  isAnalyzing.value = true;
  dMsg.text = ''; // Clear previous messages
  diagResult.value = null; // Clear previous result
  
  try {
    diagResult.value = await apiCall(`/api/patients/${dForm.patientId}/diagnose`, { method: "POST", body: fd });
    dMsg.text = t('diag_saved'); dMsg.ok = true;
  } catch (e) {
    dMsg.text = e.message; dMsg.ok = false;
  } finally {
    isAnalyzing.value = false;
  }
};

// Records
const loadRecords = async (page) => {
  if (page < 0) return;
  let url = `/api/records?page=${page}&size=10`;
  if (rFilters.label) url += `&label=${rFilters.label}`;
  if (rFilters.from) url += `&from=${rFilters.from}`;
  if (rFilters.to) url += `&to=${rFilters.to}`;
  
  try {
    const d = await apiCall(url);
    records.value = d.content || [];
    rPage.value = d.number;
    Object.assign(rPageObj, d);
    rMsg.text = ''; // Clear errors
  } catch (e) {
    records.value = [];
    rMsg.text = e.message; rMsg.ok = false;
  }
};

// INIT
onMounted(() => {
  loadStats();
});
</script>

<style>
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

  :root {
    /* Upgraded Contrast & Medical Palette */
    --bg: #F8F9FA; 
    --surface: #ffffff; 
    --ink: #1f2937; 
    --muted: #6b7280; 
    --line: #e5e7eb;
    --rose: #d6447a; 
    --rose-deep: #9c2d57; 
    --rose-soft: #fbe6ee; 
    --plum: #5e2747;
    /* WCAG Compliant Accessibility Colors */
    --mal: #DC2626; 
    --mal-soft: #fef2f2; 
    --ben: #16A34A; 
    --ben-soft: #f0fdf4;
    --shadow: 0 1px 2px rgba(0,0,0,.05), 0 8px 24px rgba(0,0,0,.04);
  }
  
  * { box-sizing: border-box; margin: 0; padding: 0; }
  
  body { 
    font-family: "Inter", system-ui, sans-serif; 
    background: var(--bg); 
    color: var(--ink); 
    -webkit-font-smoothing: antialiased; 
    line-height: 1.5; 
  }
  
  .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; }
  h1, h2, h3, .brand-name { font-family: "Inter", system-ui, sans-serif; font-weight: 700; letter-spacing: -0.02em; }

  header { background: linear-gradient(125deg, var(--plum), var(--rose)); color: #fff; padding: 0 26px; box-shadow: var(--shadow); position: sticky; top: 0; z-index: 10; }
  .bar { display: flex; align-items: center; gap: 22px; max-width: 1200px; margin: 0 auto; min-height: 70px; flex-wrap: wrap; padding: 8px 0; }
  .brand { display: flex; align-items: center; gap: 12px; }
  .brand svg { width: 34px; height: 34px; flex: 0 0 auto; }
  .brand-name { font-size: 21px; line-height: 1; }
  .brand-tag { font-size: 11.5px; color: #f6d6e3; letter-spacing: .02em; font-weight: 500;}
  
  nav { display: flex; gap: 4px; margin-left: auto; }
  nav button { background: transparent; border: 0; color: #f3d4e1; font: inherit; font-weight: 600; padding: 8px 15px; border-radius: 9px; cursor: pointer; transition: .15s; }
  nav button:hover { background: rgba(255,255,255,.12); color: #fff; }
  nav button.active { background: #fff; color: var(--rose-deep); }
  
  .lang { display: flex; align-items: center; gap: 7px; color: #fff; }
  .lang svg { width: 16px; height: 16px; opacity: .85; }
  .lang select { background: rgba(255,255,255,.14); color: #fff; border: 1px solid rgba(255,255,255,.3); border-radius: 8px; padding: 7px 9px; font: inherit; font-size: 13px; font-weight: 500; cursor: pointer; }
  .lang select option { color: var(--ink); }

  main { max-width: 1200px; margin: 32px auto; padding: 0 26px; }
  section { display: none; animation: rise .35s ease both; }
  section.active { display: block; }
  @keyframes rise { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: none; } }

  .head { margin-bottom: 24px; }
  .head h2 { font-size: 28px; color: var(--ink); }
  .head p { color: var(--muted); font-size: 14px; margin-top: 4px; }
  .card { background: var(--surface); border: 1px solid var(--line); border-radius: 12px; box-shadow: var(--shadow); }
  .pad { padding: 24px; }

  /* Dashboard Cards Upgrade */
  .stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
  .stat { padding: 20px; }
  .stat-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;}
  .stat .label { font-size: 12px; text-transform: uppercase; letter-spacing: .08em; color: var(--muted); font-weight: 600; }
  .stat-icon { width: 20px; height: 20px; color: #9ca3af; }
  .mal-icon { color: var(--mal); }
  .ben-icon { color: var(--ben); }
  .stat .value { font-size: 36px; font-weight: 700; color: var(--ink); line-height: 1.2;}
  .stat.mal .value { color: var(--mal); } 
  .stat.ben .value { color: var(--ben); }
  
  /* Axis Upgrade */
  .bars { display: flex; flex-direction: column; gap: 14px; margin-top: 12px; position: relative;}
  .barrow { display: grid; grid-template-columns: 175px 1fr 44px; align-items: center; gap: 12px; font-size: 14px; font-weight: 500; color: var(--ink);}
  .track { height: 10px; background: var(--line); border-radius: 99px; overflow: hidden; }
  .fill { height: 100%; background: linear-gradient(90deg, var(--rose), #f472b6); border-radius: 99px; transition: width .6s; }
  .axis { display: grid; grid-template-columns: 175px 1fr 44px; gap: 12px; margin-top: 4px; border-top: 1px solid var(--line); padding-top: 8px;}
  .axis-ticks { display: flex; justify-content: space-between; font-size: 11px; color: var(--muted); font-weight: 500;}

  /* Tables */
  table { width: 100%; border-collapse: collapse; font-size: 14px; }
  th { text-align: left; font-size: 11px; letter-spacing: .06em; text-transform: uppercase; color: var(--muted); padding: 14px; border-bottom: 2px solid var(--line); font-weight: 600;}
  td { padding: 14px; border-bottom: 1px solid var(--line); vertical-align: middle;}
  tr:last-child td { border-bottom: 0; }
  tbody tr:hover { background: #f9fafb; }
  
  /* Badges */
  .badge { display: inline-flex; align-items: center; padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: 600; }
  .badge.large { font-size: 16px; padding: 6px 16px; }
  .badge.mal { background: var(--mal-soft); color: var(--mal); } 
  .badge.ben { background: var(--ben-soft); color: var(--ben); }
  .badge.solid.mal { background: var(--mal); color: #fff; }
  .badge.solid.ben { background: var(--ben); color: #fff; }

  /* Forms */
  .grid2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
  label.f { display: block; font-size: 13px; font-weight: 600; margin-bottom: 8px; color: var(--ink);}
  input, select.in { width: 100%; padding: 10px 14px; border: 1px solid #d1d5db; border-radius: 8px; font: inherit; background: #fff; transition: .15s; font-size: 14px;}
  input:focus, select.in:focus { outline: 0; border-color: var(--rose); box-shadow: 0 0 0 3px var(--rose-soft); }
  
  /* Buttons & Actions */
  .btn { background: var(--rose); color: #fff; border: 0; border-radius: 8px; padding: 10px 20px; font: inherit; font-weight: 600; cursor: pointer; transition: .15s; }
  .btn:hover { background: var(--rose-deep); } 
  .btn.ghost { background: #fff; color: var(--ink); border: 1px solid #d1d5db; }
  .btn.ghost:hover { background: #f3f4f6; }
  .btn:disabled { opacity: .55; cursor: not-allowed; }
  
  .table-actions { display: flex; gap: 8px; justify-content: flex-end; }
  .icon-btn { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 6px; border: 1px solid var(--line); background: #fff; color: var(--muted); cursor: pointer; transition: 0.2s;}
  .icon-btn:hover { background: #f3f4f6; color: var(--ink); }
  .icon-btn.danger:hover { background: var(--mal-soft); color: var(--mal); border-color: var(--mal-soft);}
  
  .toolbar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
  .toolbar input, .toolbar select.in { width: auto; } 
  .toolbar .search { flex: 1 1 240px; }
  
  /* Drop Zone */
  .file-hidden { opacity: 0; width: 0.1px; height: 0.1px; position: absolute; }
  .drop-zone-content { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 32px; border: 2px dashed #d1d5db; border-radius: 8px; background: #fafafa; cursor: pointer; transition: 0.2s; text-align: center;}
  .drop-zone-content:hover { border-color: var(--rose); background: var(--rose-soft); }
  .drop-icon { width: 32px; height: 32px; color: var(--muted); margin-bottom: 12px; }
  .drop-zone-content:hover .drop-icon { color: var(--rose); }
  .drop-text { font-size: 14px; font-weight: 500; color: var(--muted); }
  .file-selected { color: var(--rose); font-weight: 600; }

  /* Misc */
  .msg { padding: 12px 16px; border-radius: 8px; font-size: 14px; margin-bottom: 16px; display: none; font-weight: 500;}
  .msg.ok { display: block; background: var(--ben-soft); color: var(--ben); border: 1px solid #bbf7d0;}
  .msg.err { display: block; background: var(--mal-soft); color: var(--mal); border: 1px solid #fecaca;}
  
  .pager { display: flex; align-items: center; gap: 16px; justify-content: flex-end; margin-top: 16px; font-size: 14px; color: var(--muted); font-weight: 500;}

  /* Results Hierarchy */
  .result { margin-top: 24px; border-left: 6px solid var(--line); }
  .result.mal { border-left-color: var(--mal); background: linear-gradient(to right, var(--mal-soft) 0%, transparent 100%);} 
  .result.ben { border-left-color: var(--ben); background: linear-gradient(to right, var(--ben-soft) 0%, transparent 100%);}
  .result-header { display: flex; align-items: center; gap: 16px; margin-bottom: 12px;}
  .result h3 { font-size: 20px; margin: 0;}
  
  .kv { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; margin-top: 16px; padding-top: 16px; border-top: 1px solid rgba(0,0,0,0.05);}
  .kv .k { font-size: 12px; color: var(--muted); text-transform: uppercase; letter-spacing: .06em; font-weight: 600;}
  .kv .v { font-size: 18px; font-weight: 600; margin-top: 4px; color: var(--ink);}
  .highlight-pct { font-size: 24px !important; font-weight: 700 !important; }
  .note { font-size: 13px; color: var(--muted); margin-top: 20px; }
  
  .image-cell { display: flex; align-items: center; gap: 8px; }
  .img-icon { width: 16px; height: 16px; color: var(--muted); flex-shrink: 0;}
  
  @media(max-width: 760px) {
    .stats { grid-template-columns: 1fr 1fr; }
    .grid2 { grid-template-columns: 1fr; }
    .kv { grid-template-columns: 1fr; }
    .barrow { grid-template-columns: 120px 1fr 40px; }
    .axis { grid-template-columns: 120px 1fr 40px; }
  }
</style>