<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t('paramManagement.pageTitle') }}</h2>
      <div class="right-operations">
        <el-input
          v-model="searchCode"
          :placeholder="$t('paramManagement.searchPlaceholder')"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t("common.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="params-card" shadow="never">
            <el-table
              ref="paramsTable"
              :data="paramsList"
              class="transparent-table"
              v-loading="loading"
              height="100%"
              :element-loading-text="$t('common.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
              :header-cell-class-name="headerCellClassName"
            >
              <el-table-column align="center" width="80">
                <template slot="header">
                  <span class="selection-header-text">{{ $t('modelConfig.select') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.selected"></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('paramManagement.paramCode')"
                prop="paramCode"
                align="center"
              >
                <template slot-scope="scope">
                  <span class="param-code-text">{{ scope.row.paramCode }}</span>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('paramManagement.paramValue')"
                prop="paramValue"
                align="center"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <div v-if="isSensitiveParam(scope.row.paramCode)" class="sensitive-value-container">
                    <span class="param-value-text" v-if="!scope.row.showValue">
                      {{ maskSensitiveValue(scope.row.paramValue) }}
                    </span>
                    <span class="param-value-text" v-else>{{ scope.row.paramValue }}</span>
                    
                    <el-button
                      size="mini"
                      type="text"
                      class="toggle-visibility-btn"
                      @click="toggleSensitiveValue(scope.row)"
                    >
                      <i :class="scope.row.showValue ? 'el-icon-turn-off' : 'el-icon-open'"></i>
                      {{ scope.row.showValue ? $t("paramManagement.hide") : $t("paramManagement.view") }}
                    </el-button>
                  </div>
                  <span v-else class="param-value-text">{{ scope.row.paramValue }}</span>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('paramManagement.remark')"
                prop="remark"
                align="center"
              ></el-table-column>

              <el-table-column
                :label="$t('paramManagement.operation')"
                align="center"
                width="180"
              >
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="editParam(scope.row)">
                    {{ $t("paramManagement.edit") }}
                  </el-button>
                  <el-button size="mini" type="text" class="delete-btn" @click="deleteParam(scope.row)">
                    {{ $t("paramManagement.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button size="mini" type="primary" class="select-all-btn" @click="handleSelectAll">
                  {{ isAllSelected ? $t("paramManagement.deselectAll") : $t("paramManagement.selectAll") }}
                </el-button>
                <el-button size="mini" type="success" @click="showAddDialog">
                  {{ $t("paramManagement.add") }}
                </el-button>
                <el-button size="mini" type="danger" :disabled="!hasSelected" @click="deleteSelectedParams">
                  {{ $t("paramManagement.delete") }}
                </el-button>
              </div>

              <div class="custom-pagination">
                <el-select v-model="pageSize" @change="handlePageSizeChange" class="page-size-select aurora-select">
                  <el-option
                    v-for="item in pageSizeOptions"
                    :key="item"
                    :label="`${item}${$t('paramManagement.itemsPerPage')}`"
                    :value="item"
                  ></el-option>
                </el-select>
                <button class="pagination-btn" :disabled="currentPage === 1" @click="goFirst">
                  {{ $t("paramManagement.firstPage") }}
                </button>
                <button class="pagination-btn" :disabled="currentPage === 1" @click="goPrev">
                  {{ $t("paramManagement.prevPage") }}
                </button>
                <button
                  v-for="page in visiblePages"
                  :key="page"
                  class="pagination-btn"
                  :class="{ active: page === currentPage }"
                  @click="goToPage(page)"
                >
                  {{ page }}
                </button>
                <button class="pagination-btn" :disabled="currentPage === pageCount" @click="goNext">
                  {{ $t("paramManagement.nextPage") }}
                </button>
                <span class="total-text">{{ $t("paramManagement.totalRecords", { total }) }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <param-dialog
      ref="paramDialog"
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :form="paramForm"
      @submit="handleSubmit"
      @cancel="dialogVisible = false"
    />
    
    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import Api from "@/apis/api";
import ParamDialog from "@/components/ParamDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";

export default {
  name: "ParamsManagement",
  components: { ParamDialog, VersionFooter },
  data() {
    return {
      searchCode: "",
      paramsList: [],
      currentPage: 1,
      loading: false,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      total: 0,
      dialogVisible: false,
      dialogTitle: "",
      isAllSelected: false,
      sensitive_keys: [
        "api_key",
        "personal_access_token",
        "access_token",
        "token",
        "secret",
        "access_key_secret",
        "secret_key",
        "password",
        "mqtt_signature_key",
        "private_key",
      ],
      paramForm: {
        id: null,
        paramCode: "",
        paramValue: "",
        valueType: "string",
        remark: "",
      },
    };
  },
  created() {
    this.fetchParams();
  },
  mounted() {
    this.dialogTitle = this.$t("paramManagement.addParam");
  },
  computed: {
    hasSelected() {
      return this.paramsList.some(row => row.selected);
    },
    pageCount() {
      return Math.ceil(this.total / this.pageSize);
    },
    visiblePages() {
      const pages = [];
      const maxVisible = 3;
      let start = Math.max(1, this.currentPage - 1);
      let end = Math.min(this.pageCount, start + maxVisible - 1);

      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    },
  },
  methods: {
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchParams();
    },
    fetchParams() {
      this.loading = true;
      Api.admin.getParamsList({
        page: this.currentPage,
        limit: this.pageSize,
        paramCode: this.searchCode,
      }, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.paramsList = data.data.list.map((item) => ({
            ...item,
            valueType: item.valueType || "string",
            selected: false,
            showValue: false,
          }));
          this.total = data.data.total;
        } else {
          this.$message.error({
            message: data.msg || this.$t("paramManagement.getParamsListFailed"),
            showClose: true,
          });
        }
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchParams();
    },
    handleSelectAll() {
      this.isAllSelected = !this.isAllSelected;
      this.paramsList.forEach((row) => (row.selected = this.isAllSelected));
    },
    showAddDialog() {
      this.dialogTitle = this.$t("paramManagement.addParam");
      this.paramForm = { id: null, paramCode: "", paramValue: "", valueType: "string", remark: "" };
      this.dialogVisible = true;
    },
    editParam(row) {
      this.dialogTitle = this.$t("paramManagement.editParam");
      this.paramForm = { id: row.id, paramCode: row.paramCode, paramValue: row.paramValue, valueType: row.valueType || "string", remark: row.remark };
      this.dialogVisible = true;
    },
    handleSubmit(form) {
      const apiMethod = form.id ? Api.admin.updateParam : Api.admin.addParam;
      apiMethod(form, ({ data }) => {
        if (data.code === 0) {
          this.dialogVisible = false;
          this.fetchParams();
          this.$message.success({
            message: form.id ? this.$t("paramManagement.updateSuccess") : this.$t("paramManagement.addSuccess"),
            showClose: true,
          });
        } else {
          this.$message.error({ message: data.msg || (form.id ? this.$t("paramManagement.updateFailed") : this.$t("paramManagement.addFailed")), showClose: true });
          if (this.$refs.paramDialog?.resetSaving) this.$refs.paramDialog.resetSaving();
        }
      }, ({ data }) => {
        this.$message.error({ message: data.msg || this.$t("paramManagement.updateFailed"), showClose: true });
        if (this.$refs.paramDialog?.resetSaving) this.$refs.paramDialog.resetSaving();
      });
    },
    deleteSelectedParams() {
      const selectedParams = this.paramsList.filter((row) => row.selected);
      if (selectedParams.length === 0) return this.$message.warning(this.$t("paramManagement.selectParamsFirst"));
      this.deleteParams(selectedParams);
    },
    deleteParam(row) {
      if (!row.id) return this.$message.warning(this.$t("paramManagement.selectParamsFirst"));
      this.deleteParams([row]);
    },
    deleteParams(params) {
      const paramIds = params.map((p) => p.id).filter(id => id);
      if (paramIds.length === 0) return this.$message.error(this.$t("paramManagement.invalidParamId"));
      
      this.$confirm(this.$t("paramManagement.confirmBatchDelete", { paramCount: params.length }), this.$t("message.warning"), {
        type: "warning",
      }).then(() => {
        Api.admin.deleteParam(paramIds, ({ data }) => {
          if (data.code === 0) {
            this.fetchParams();
            this.$message.success(this.$t("paramManagement.batchDeleteSuccess", { paramCount: params.length }));
          } else {
            this.$message.error(data.msg || this.$t("paramManagement.deleteFailed"));
          }
        });
      }).catch(() => {});
    },
    goToPage(page) {
      if (page !== this.currentPage) { this.currentPage = page; this.fetchParams(); }
    },
    goFirst() {
      if (this.currentPage !== 1) { this.currentPage = 1; this.fetchParams(); }
    },
    goPrev() {
      if (this.currentPage > 1) { this.currentPage--; this.fetchParams(); }
    },
    goNext() {
      if (this.currentPage < this.pageCount) { this.currentPage++; this.fetchParams(); }
    },
    isSensitiveParam(paramCode) {
      return this.sensitive_keys.some((key) => paramCode.toLowerCase().includes(key));
    },
    maskSensitiveValue(value) {
      if (!value) return "";
      if (value.length <= 4) return "****";
      return value.substring(0, 2) + "****" + value.substring(value.length - 2);
    },
    toggleSensitiveValue(row) {
      row.showValue = !row.showValue;
    },
    headerCellClassName({ columnIndex }) {
      return columnIndex === 0 ? "custom-selection-header" : "";
    },
  },
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for ParamsManagement --- */
.param-code-text { color: $accent-cyan; font-weight: bold; }
.param-value-text { color: $accent-purple; }
.sensitive-value-container { display: flex; align-items: center; justify-content: center; gap: 8px; }
.toggle-visibility-btn { color: $text-muted !important; &:hover { color: $accent-cyan !important; } }
</style>
