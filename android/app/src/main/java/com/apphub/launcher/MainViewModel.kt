package com.apphub.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apphub.launcher.domain.AppEntry
import com.apphub.launcher.domain.Category
import com.apphub.launcher.domain.EntryRepository
import com.apphub.launcher.domain.EntryType
import com.apphub.launcher.domain.InstalledApp
import com.apphub.launcher.data.InstalledAppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo: EntryRepository,
    private val installedRepo: InstalledAppRepository,
) : ViewModel() {

    private val _activeCategory = MutableStateFlow("全部")
    val activeCategory: StateFlow<String> = _activeCategory.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // 编辑对话框状态（null=关闭）
    private val _editing = MutableStateFlow<AppEntry?>(null)
    val editing: StateFlow<AppEntry?> = _editing.asStateFlow()

    // 导入导出对话框状态
    private val _showIO = MutableStateFlow(false)
    val showIO: StateFlow<Boolean> = _showIO.asStateFlow()

    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val toast: SharedFlow<String> = _toast

    fun sendToast(msg: String) {
        _toast.tryEmit(msg)
    }

    private val _installed = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installed: StateFlow<List<InstalledApp>> = _installed

    @OptIn(ExperimentalCoroutinesApi::class)
    val visibleEntries: StateFlow<List<AppEntry>> =
        combine(repo.entries(), _activeCategory, _query) { list, cat, q ->
            val byCat = if (cat == "全部") list else list.filter { it.category == cat }
            if (q.isBlank()) byCat else {
                val key = q.lowercase()
                byCat.filter {
                    it.name.lowercase().contains(key) ||
                        it.url.lowercase().contains(key) ||
                        it.pkg.lowercase().contains(key)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val categories: StateFlow<List<Pair<String, Int>>> =
        repo.entries().mapLatest { list ->
            val counts = list.groupingBy { it.category }.eachCount()
            (listOf("全部" to list.size) + Category.ALL.map { c -> c to (counts[c] ?: 0) })
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        refreshInstalled()
    }

    fun refreshInstalled() {
        viewModelScope.launch {
            _installed.value = installedRepo.listAll()
        }
    }

    fun setCategory(c: String) { _activeCategory.value = c }
    fun setQuery(q: String) { _query.value = q }

    fun startAdd(type: EntryType = EntryType.Web) {
        _editing.value = AppEntry(
            id = "",
            name = "",
            type = type,
            category = Category.DEFAULT,
        )
    }
    fun startEdit(entry: AppEntry) { _editing.value = entry }
    fun cancelEdit() { _editing.value = null }

    fun saveDraft(
        name: String,
        type: EntryType,
        url: String,
        pkg: String,
        icon: String,
        category: String,
    ): Boolean {
        val n = name.trim()
        if (n.isBlank()) { sendToast("请填写名称"); return false }
        val draft = _editing.value
        val finalUrl = if (type == EntryType.Web && url.isNotBlank() &&
            !url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else url
        val newOrExisting = draft?.copy(
            name = n, type = type,
            url = if (type == EntryType.Web) finalUrl else "",
            pkg = if (type == EntryType.Native) pkg.trim() else "",
            icon = icon.ifBlank { "📦" },
            category = category,
            order = draft.order,
        ) ?: AppEntry(
            id = "a-${System.currentTimeMillis()}",
            name = n, type = type,
            url = if (type == EntryType.Web) finalUrl else "",
            pkg = if (type == EntryType.Native) pkg.trim() else "",
            icon = icon.ifBlank { "📦" },
            category = category,
        )
        viewModelScope.launch { repo.upsert(newOrExisting) }
        _editing.value = null
        return true
    }

    fun deleteEditing() {
        val e = _editing.value ?: return
        viewModelScope.launch { repo.delete(e.id) }
        _editing.value = null
        sendToast("已删除")
    }

    fun launch(e: AppEntry) {
        runCatching { repo.launch(e) }.onFailure {
            sendToast("无法启动 ${e.name}")
        }
    }

    // ----- IO -----
    fun toggleIO(show: Boolean) { _showIO.value = show }
    suspend fun exportText(): String = repo.toJson(repo.getAll())
    fun importText(text: String) {
        val list = repo.fromJson(text)
        if (list == null) {
            sendToast("导入失败：解析错误")
            return
        }
        viewModelScope.launch {
            repo.replaceAll(list)
            sendToast("已导入 ${list.size} 个条目")
            _showIO.value = false
        }
    }
}

class MainViewModelFactory(
    private val repo: EntryRepository,
    private val installed: InstalledAppRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainViewModel(repo, installed) as T
}
