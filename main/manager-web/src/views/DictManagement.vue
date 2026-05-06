<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("dictManagement.pageTitle") }}</h2>
      <div class="right-operations">
        <el-select
          v-model="selectedDictTypeId"
          class="dict-type-select aurora-select"
          :placeholder="$t('dictManagement.dictTypeName')"
          @change="handleDictTypeChange"
        >
          <el-option
            v-for="item in dictTypeList"
            :key="item.id"
            :label="item.dictName"
            :value="item.id"
          />
        </el-select>
        <el-input
          v-model="search"
          :placeholder="$t('dictManagement.searchPlaceholder')"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t("dictManagement.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="dict-data-card" shadow="never">
            <el-table
              ref="dictDataTable"
              :data="dictDataList"
              v-loading="dictDataLoading"
              class="transparent-table"
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
                :label="$t('dictManagement.dictLabel')"
                prop="dictLabel"
                align="center"
              />
              
              <el-table-column
                :label="$t('dictManagement.dictValue')"
                prop="dictValue"
                align="center"
              >
                <template slot-scope="scope">
                  <span class="value-text">{{ scope.row.dictValue }}</span>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('dictManagement.sort')"
                prop="sort"
                align="center"
                width="100"
              />

              <el-table-column
                :label="$t('dictManagement.operation')"
                align="center"
                width="180"
              >
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="editDictData(scope.row)">
                    {{ $t("dictManagement.edit") }}
                  </el-button>
                  <el-button type="text" size="mini" class="delete-btn" @click="deleteDictData(scope.row)">
                    {{ $t("dictManagement.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button type="primary" size="mini" @click="selectAllDictData">
                  {{ isAllDictDataSelected ? $t("dictManagement.deselectAll") : $t("dictManagement.selectAll") }}
                </el-button>
                <el-button type="primary" plain size="mini" class="outline-btn" @click="showAddDictTypeDialog">
                  {{ $t("dictManagement.addDictType") }}
                </el-button>
                <el-button type="success" size="mini" @click="showAddDictDataDialog">
                  {{ $t("dictManagement.addDictData") }}
                </el-button>
                <el-button type="danger" size="mini" :disabled="!hasSelected" @click="batchDeleteDictData">
                  {{ $t("dictManagement.batchDeleteDictData") }}
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
                  @current-change="handlePageChange"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <DictTypeDialog
      :visible.sync="dictTypeDialogVisible"
      :title="dictTypeDialogTitle"
      :dictTypeData="dictTypeForm"
      @save="saveDictType"
    />

    <DictDataDialog
      :visible.sync="dictDataDialogVisible"
      :title="dictDataDialogTitle"
      :dictData="dictDataForm"
      :dictTypeId="selectedDictType?.id"
      @save="saveDictData"
    />

    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import dictApi from "@/apis/module/dict";
import DictDataDialog from "@/components/DictDataDialog.vue";
import DictTypeDialog from "@/components/DictTypeDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";

export default {
  name: "DictManagement",
  components: { DictTypeDialog, DictDataDialog, VersionFooter },
  data() {
    return {
      dictTypeList: [],
      dictTypeLoading: false,
      selectedDictType: null,
      selectedDictTypeId: null,
      selectedDictTypes: [],
      dictTypeDialogVisible: false,
      dictTypeDialogTitle: "",
      dictTypeForm: { id: null, dictName: "", dictType: "" },

      dictDataList: [],
      dictDataLoading: false,
      isAllDictDataSelected: false,
      dictDataDialogVisible: false,
      dictDataDialogTitle: "",
      dictDataForm: { id: null, dictTypeId: null, dictLabel: "", dictValue: "", sort: 0 },
      search: "",
      pageSizeOptions: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
    };
  },
  computed: {
    hasSelected() {
      return this.dictDataList.some(row => row.selected);
    }
  },
  created() {
    this.loadDictTypeList();
  },
  methods: {
    loadDictTypeList() {
      this.dictTypeLoading = true;
      dictApi.getDictTypeList({ page: 1, limit: 100 }, ({ data }) => {
        if (data.code === 0) {
          this.dictTypeList = data.data.list;
          if (this.dictTypeList.length > 0) {
            const preferred = this.dictTypeList.find(i => i.id === this.selectedDictTypeId) || this.dictTypeList[0];
            this.selectedDictType = preferred;
            this.selectedDictTypeId = preferred.id;
            this.loadDictDataList(preferred.id);
          }
        }
        this.dictTypeLoading = false;
      });
    },
    handleDictTypeChange(id) {
      this.selectedDictType = this.dictTypeList.find(i => i.id === id);
      this.currentPage = 1;
      this.loadDictDataList(id);
    },
    loadDictDataList(dictTypeId) {
      if (!dictTypeId) return;
      this.dictDataLoading = true;
      dictApi.getDictDataList({
        dictTypeId,
        page: this.currentPage,
        limit: this.pageSize,
        dictLabel: this.search,
      }, ({ data }) => {
        if (data.code === 0) {
          this.dictDataList = data.data.list.map(i => ({ ...i, selected: false }));
          this.total = data.data.total;
        }
        this.dictDataLoading = false;
      });
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.loadDictDataList(this.selectedDictTypeId);
    },
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.loadDictDataList(this.selectedDictTypeId);
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadDictDataList(this.selectedDictTypeId);
    },
    selectAllDictData() {
      this.isAllDictDataSelected = !this.isAllDictDataSelected;
      this.dictDataList.forEach(row => (row.selected = this.isAllDictDataSelected));
    },
    showAddDictTypeDialog() {
      this.dictTypeDialogTitle = this.$t("dictManagement.addDictType");
      this.dictTypeForm = { id: null, dictName: "", dictType: "" };
      this.dictTypeDialogVisible = true;
    },
    showAddDictDataDialog() {
      if (!this.selectedDictType) return;
      this.dictDataDialogTitle = this.$t("dictManagement.addDictData");
      this.dictDataForm = { id: null, dictTypeId: this.selectedDictType.id, dictLabel: "", dictValue: "", sort: 0 };
      this.dictDataDialogVisible = true;
    },
    editDictData(row) {
      this.dictDataDialogTitle = this.$t("dictManagement.editDictData");
      this.dictDataForm = { ...row };
      this.dictDataDialogVisible = true;
    },
    saveDictType(formData) {
      const api = formData.id ? dictApi.updateDictType : dictApi.addDictType;
      api(formData, ({ data }) => {
        if (data.code === 0) {
          this.$message.success(this.$t("dictManagement.saveSuccess"));
          this.dictTypeDialogVisible = false;
          this.loadDictTypeList();
        }
      });
    },
    saveDictData(formData) {
      const api = formData.id ? dictApi.updateDictData : dictApi.addDictData;
      api(formData, ({ data }) => {
        if (data.code === 0) {
          this.$message.success(this.$t("dictManagement.saveSuccess"));
          this.dictDataDialogVisible = false;
          this.loadDictDataList(this.selectedDictTypeId);
        }
      });
    },
    deleteDictData(row) {
      this.$confirm(this.$t("dictManagement.confirmDeleteDictData"), "Warning", { type: "warning" }).then(() => {
        dictApi.deleteDictData([row.id], () => this.loadDictDataList(this.selectedDictTypeId));
      });
    },
    batchDeleteDictData() {
      const selected = this.dictDataList.filter(r => r.selected);
      this.$confirm(this.$t("dictManagement.confirmBatchDeleteDictData", { count: selected.length }), "Warning", { type: "warning" }).then(() => {
        dictApi.deleteDictData(selected.map(i => i.id), () => this.loadDictDataList(this.selectedDictTypeId));
      });
    },
    headerCellClassName({ columnIndex }) {
      return columnIndex === 0 ? "custom-selection-header" : "";
    }
  }
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for DictManagement --- */
.dict-code-text { color: $accent-cyan; font-weight: bold; }
</style>
