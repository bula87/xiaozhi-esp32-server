<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("otaManagement.firmwareManagement") }}</h2>
      <div class="right-operations">
        <el-input
          v-model="searchName"
          :placeholder="$t('otaManagement.searchPlaceholder')"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t("otaManagement.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="template-card" shadow="never">
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

              <el-table-column :label="$t('otaManagement.firmwareName')" prop="firmwareName" align="center">
                <template slot-scope="scope">
                  <span class="firmware-name-text">{{ scope.row.firmwareName }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('otaManagement.firmwareType')" align="center" width="120">
                <template slot-scope="scope">
                  <el-tag size="mini" effect="dark" class="aurora-tag">
                    {{ getFirmwareTypeName(scope.row.type) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column :label="$t('otaManagement.version')" prop="version" align="center" width="100">
                <template slot-scope="scope">
                  <span class="version-text">{{ scope.row.version }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('otaManagement.fileSize')" align="center" width="100">
                <template slot-scope="scope">
                  <span class="size-text">{{ formatFileSize(scope.row.size) }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('otaManagement.remark')" prop="remark" align="center" show-overflow-tooltip />

              <el-table-column :label="$t('otaManagement.updateTime')" align="center" width="180">
                <template slot-scope="scope">
                  <span class="time-text">{{ formatDate(scope.row.updateDate) }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('otaManagement.action')" align="center" width="240">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" class="download-btn" @click="downloadFirmware(scope.row)">
                    {{ $t("otaManagement.download") }}
                  </el-button>
                  <el-button type="text" size="mini" @click="editParam(scope.row)">
                    {{ $t("otaManagement.edit") }}
                  </el-button>
                  <el-button type="text" size="mini" class="delete-btn" @click="deleteParam(scope.row)">
                    {{ $t("otaManagement.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button type="primary" size="mini" @click="handleSelectAll">
                  {{ isAllSelected ? $t("otaManagement.deselectAll") : $t("otaManagement.selectAll") }}
                </el-button>
                <el-button type="success" size="mini" @click="showAddDialog">
                  {{ $t("otaManagement.addNew") }}
                </el-button>
                <el-button type="danger" size="mini" @click="deleteSelectedParams">
                  {{ $t("otaManagement.delete") }}
                </el-button>
              </div>

              <div class="custom-pagination">
                <el-pagination
                  background
                  layout="total, sizes, prev, pager, next"
                  :current-page.sync="currentPage"
                  :page-size.sync="pageSize"
                  :total="total"
                  :page-sizes="pageSizeOptions"
                  @size-change="handlePageSizeChange"
                  @current-change="fetchFirmwareList"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <firmware-dialog
      ref="firmwareDialog"
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :form="firmwareForm"
      :firmware-types="firmwareTypes"
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
import FirmwareDialog from "@/components/FirmwareDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";
import { formatDate, formatFileSize } from "@/utils/format";

export default {
  name: "OtaManagement", // Standardized Name
  components: { FirmwareDialog, VersionFooter },
  data() {
    return {
      searchName: "",
      loading: false,
      paramsList: [],
      firmwareList: [],
      currentPage: 1,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      total: 0,
      dialogVisible: false,
      dialogTitle: "",
      isAllSelected: false,
      firmwareForm: {
        id: null,
        firmwareName: "",
        type: "",
        version: "",
        size: 0,
        remark: "",
        firmwarePath: "",
      },
      firmwareTypes: [],
    };
  },
  created() {
    this.fetchFirmwareList();
    this.getFirmwareTypes();
  },
  methods: {
    formatDate,
    formatFileSize,
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchFirmwareList();
    },
    fetchFirmwareList() {
      this.loading = true;
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        firmwareName: this.searchName || "",
        orderField: "create_date",
        order: "desc",
      };
      Api.ota.getOtaList(params, (res) => {
        this.loading = false;
        const data = res.data;
        if (data.code === 0) {
          this.firmwareList = (data.data.list || []).map((item) => ({ ...item, selected: false }));
          this.paramsList = this.firmwareList;
          this.total = data.data.total || 0;
        }
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchFirmwareList();
    },
    handleSelectAll() {
      this.isAllSelected = !this.isAllSelected;
      this.firmwareList.forEach((row) => (row.selected = this.isAllSelected));
    },
    showAddDialog() {
      this.dialogTitle = this.$t("otaManagement.addFirmware");
      this.firmwareForm = { id: null, firmwareName: "", type: "", version: "", size: 0, remark: "", firmwarePath: "" };
      this.dialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.firmwareDialog?.$refs.form) this.$refs.firmwareDialog.$refs.form.clearValidate();
      });
    },
    editParam(row) {
      this.dialogTitle = this.$t("otaManagement.editFirmware");
      this.firmwareForm = { ...row };
      this.dialogVisible = true;
    },
    handleSubmit(form) {
      const apiMethod = form.id ? Api.ota.updateOta : Api.ota.saveOta;
      const params = form.id ? [form.id, form] : [form];
      
      apiMethod(...params, (res) => {
        if (res.data.code === 0) {
          this.$message.success(this.$t("otaManagement.updateSuccess"));
          this.dialogVisible = false;
          this.fetchFirmwareList();
        }
      });
    },
    deleteSelectedParams() {
      const selected = this.firmwareList.filter((row) => row.selected);
      if (selected.length === 0) return this.$message.warning(this.$t("otaManagement.selectFirmwareFirst"));
      this.deleteParam(selected);
    },
    deleteParam(row) {
      const items = Array.isArray(row) ? row : [row];
      this.$confirm(this.$t("otaManagement.confirmBatchDelete", { paramCount: items.length }), "Warning", { type: "warning" }).then(() => {
        Api.ota.deleteOta(items.map(i => i.id), (res) => {
          if (res.data.code === 0) {
            this.$message.success(this.$t("otaManagement.batchDeleteSuccess"));
            this.fetchFirmwareList();
          }
        });
      });
    },
    downloadFirmware(firmware) {
      Api.ota.getDownloadUrl(firmware.id, (res) => {
        if (res.data.code === 0) {
          const baseUrl = process.env.VUE_APP_API_BASE_URL || "";
          window.open(`${window.location.origin}${baseUrl}/otaMag/download/${res.data.data}`);
        }
      });
    },
    async getFirmwareTypes() {
      const res = await Api.dict.getDictDataByType("FIRMWARE_TYPE");
      this.firmwareTypes = res.data;
    },
    getFirmwareTypeName(type) {
      return this.firmwareTypes.find((i) => i.key === type)?.name || type;
    },
    headerCellClassName({ columnIndex }) {
      return columnIndex === 0 ? "custom-selection-header" : "";
    }
  },
};
</script>

<style lang="scss" scoped>
@import "@/styles/aurora-theme.scss";

/* ONLY COMPONENT-SPECIFIC STYLES HERE.
   All layout and standard table styling is now global in aurora-layout.scss
*/

.firmware-name-text { color: $accent-cyan; font-weight: bold; }
.version-text { color: $accent-purple; font-weight: bold; }
.size-text, .time-text { color: $text-muted; font-size: 12px; }


</style>