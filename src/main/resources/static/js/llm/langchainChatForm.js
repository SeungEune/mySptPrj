let chatHistory = [];
let isSending = false;
let promptRoleList = [];
let currentChatSessionId = null;
let currentPromptRoleCd = 'DEV';
const MAX_CHAT_INPUT_LENGTH = 10000;

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    fn_bindEvent();
});

function fn_init() {
    const modelNameEl = document.getElementById('modelName');
    if (modelNameEl && AI_MODEL_NAME) {
        modelNameEl.textContent = AI_MODEL_NAME;
    }

    fn_loadPromptRoles();
    fn_loadSessionList();
}

function fn_bindEvent() {
    document.getElementById('sendBtn').addEventListener('click', fn_sendMessage);
    document.getElementById('resetBtn').addEventListener('click', fn_resetChat);
    document.getElementById('newChatBtn').addEventListener('click', fn_createNewChatSession);
    document.getElementById('promptRoleCd').addEventListener('change', fn_changePromptRole);
    document.getElementById('chatInput').addEventListener('keydown', (event) => {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            fn_sendMessage();
        }
    });
    document.getElementById('chatInput').addEventListener('input', fn_updateChatInputLength);
    fn_updateChatInputLength();
}

function fn_updateChatInputLength() {
    const inputEl = document.getElementById('chatInput');
    const lengthEl = document.getElementById('chatInputLength');

    if (!inputEl || !lengthEl) {
        return;
    }

    lengthEl.textContent = inputEl.value.length + ' / ' + MAX_CHAT_INPUT_LENGTH + '자';
}

function fn_loadPromptRoles() {
    callModule.call(Util.getRequestUrl('/llm/getPromptRoleList.do'), null, (result) => {
        const list = fn_extractListResult(result);
        promptRoleList = list;
        fn_renderPromptRoleOptions(list);
    }, true, 'GET');
}

function fn_renderPromptRoleOptions(list) {
    const selectEl = document.getElementById('promptRoleCd');
    selectEl.innerHTML = '';

    if (!list || list.length === 0) {
        selectEl.innerHTML = '<option value="DEV">개발 도우미</option>';
        fn_updatePromptRoleDesc('DEV');
        return;
    }

    list.forEach((item) => {
        const option = document.createElement('option');
        option.value = item.roleCd;
        option.textContent = item.roleNm;
        if (item.roleCd === currentPromptRoleCd) {
            option.selected = true;
        }
        selectEl.appendChild(option);
    });

    fn_updatePromptRoleDesc(selectEl.value);
}

function fn_changePromptRole() {
    currentPromptRoleCd = document.getElementById('promptRoleCd').value;
    fn_updatePromptRoleDesc(currentPromptRoleCd);
}

function fn_updatePromptRoleDesc(roleCd) {
    const target = promptRoleList.find(item => item.roleCd === roleCd);
    const descInput = document.getElementById('promptRoleDesc');
    if (!descInput) {
        return;
    }
    descInput.value = target ? target.roleDesc : '역할 설명이 표시됩니다.';
}

function fn_loadSessionList() {
    callModule.call(Util.getRequestUrl('/llm/getSessionList.do'), {}, (result) => {
        const list = fn_extractListResult(result);
        fn_renderSessionList(list);
    }, true, 'POST');
}

function fn_renderSessionList(list) {
    const sessionListArea = document.getElementById('sessionListArea');
    sessionListArea.innerHTML = '';

    if (!list || list.length === 0) {
        sessionListArea.innerHTML = '<div class="llm-session-empty">저장된 대화가 없습니다.</div>';
        return;
    }

    const normalizedList = list.map(item => ({
        ...item,
        isActive: String(item.chatSessionId) === String(currentChatSessionId)
    }));

    $('#sessionListTemplate').tmpl(normalizedList).appendTo('#sessionListArea');
}

function fn_createNewChatSession() {
    const roleCd = document.getElementById('promptRoleCd').value;
    const roleName = promptRoleList.find(item => item.roleCd === roleCd)?.roleNm || '일반';

    callModule.call(Util.getRequestUrl('/llm/createSession.do'), {
        sessionTitle: 'LangChain ' + roleName + ' 대화',
        promptRoleCd: roleCd
    }, (result) => {
        const sessionInfo = result ? result.result : null;
        if (!sessionInfo) {
            MessageUtil.error('채팅 세션 생성에 실패했습니다.');
            return;
        }

        currentChatSessionId = sessionInfo.chatSessionId;
        currentPromptRoleCd = sessionInfo.promptRoleCd;
        document.getElementById('promptRoleCd').value = currentPromptRoleCd;
        fn_updatePromptRoleDesc(currentPromptRoleCd);
        fn_updateSessionInfo(sessionInfo);
        fn_resetChatAreaOnly();
        fn_loadSessionList();
        MessageUtil.toast('새 LangChain 대화가 시작되었습니다.');
    }, true, 'POST');
}

function fn_selectSession(chatSessionId) {
    callModule.call(Util.getRequestUrl('/llm/getSessionDetail.do'), { chatSessionId: chatSessionId }, (result) => {
        const detail = result ? result.result : null;
        if (!detail || !detail.sessionInfo) {
            MessageUtil.error('대화 정보를 불러오지 못했습니다.');
            return;
        }

        currentChatSessionId = detail.sessionInfo.chatSessionId;
        currentPromptRoleCd = detail.sessionInfo.promptRoleCd;
        document.getElementById('promptRoleCd').value = currentPromptRoleCd;
        fn_updatePromptRoleDesc(currentPromptRoleCd);
        fn_updateSessionInfo(detail.sessionInfo);
        fn_renderLoadedHistory(detail.historyList || []);
        fn_loadSessionList();
    }, true, 'POST');
}

function fn_renderLoadedHistory(historyList) {
    chatHistory = [];
    const messageArea = document.getElementById('chatMessageArea');
    messageArea.innerHTML = '';

    if (!historyList || historyList.length === 0) {
        messageArea.innerHTML = '<div class="ai-message empty-message" id="emptyMessage"><div class="message-bubble">대화를 시작해보세요.</div></div>';
        return;
    }

    historyList.forEach((item) => {
        chatHistory.push({ role: item.messageRole, content: item.messageContent });
        fn_renderMessage(item.messageRole === 'user' ? 'user' : 'assistant', item.messageContent);
    });
}

function fn_updateSessionInfo(sessionInfo) {
    const roleTarget = promptRoleList.find(item => item.roleCd === sessionInfo.promptRoleCd);
    document.getElementById('chatSessionInfo').innerHTML = ''
        + '<strong>' + sessionInfo.sessionTitle + '</strong>'
        + ' <span class="session-info-meta">(' + (roleTarget ? roleTarget.roleNm : sessionInfo.promptRoleCd) + ')</span>'
        + ' <span class="session-info-date">최근 대화: ' + sessionInfo.lastUpdtDt + '</span>';
}

function fn_sendMessage() {
    if (isSending) {
        return;
    }

    const inputEl = document.getElementById('chatInput');
    const message = Util.isEmpty(inputEl.value) ? '' : inputEl.value.trim();

    if (Util.isEmpty(message)) {
        MessageUtil.alert('질문을 입력해주세요.');
        inputEl.focus();
        return;
    }

    if (message.length > MAX_CHAT_INPUT_LENGTH) {
        MessageUtil.alert('질문은 ' + MAX_CHAT_INPUT_LENGTH + '자 이하로 입력해주세요.');
        inputEl.focus();
        return;
    }

    fn_hideEmptyMessage();
    fn_renderMessage('user', message);
    chatHistory.push({ role: 'user', content: message });
    inputEl.value = '';
    fn_updateChatInputLength();

    isSending = true;
    fn_toggleSending(true);
    fn_renderLoading();

    const requestData = {
        chatSessionId: currentChatSessionId,
        promptRoleCd: document.getElementById('promptRoleCd').value,
        message: message,
        history: chatHistory.slice(0, chatHistory.length - 1)
    };

    callModule.call(Util.getRequestUrl('/llm/sendMessageLangChain.do'), requestData, (result) => {
        fn_removeLoading();

        if (result && result.status && result.status.code !== 200) {
            fn_renderErrorMessage(Util.nvl(result.status.message, 'AI 응답 처리 중 오류가 발생했습니다.'));
            fn_toggleSending(false);
            return;
        }

        const answer = result && result.result ? result.result.answer : null;
        if (Util.isEmpty(answer)) {
            fn_renderErrorMessage('AI 응답 본문이 없습니다.');
            fn_toggleSending(false);
            return;
        }

        fn_renderMessage('assistant', answer);
        chatHistory.push({ role: 'assistant', content: answer });
        fn_toggleSending(false);
        fn_loadSessionList();
    }, true, 'POST');
}

function fn_resetChat() {
    currentChatSessionId = null;
    currentPromptRoleCd = document.getElementById('promptRoleCd').value;
    document.getElementById('chatSessionInfo').textContent = '선택된 대화가 없습니다. 새 대화를 시작하거나 기존 대화를 선택하세요.';
    fn_resetChatAreaOnly();
    fn_loadSessionList();
}

function fn_resetChatAreaOnly() {
    chatHistory = [];
    const messageArea = document.getElementById('chatMessageArea');
    messageArea.innerHTML = '';
    messageArea.innerHTML = '<div class="ai-message empty-message" id="emptyMessage"><div class="message-bubble">대화를 시작해보세요.</div></div>';
    document.getElementById('chatInput').value = '';
    fn_updateChatInputLength();
}

function fn_renderMessage(role, content) {
    const messageArea = document.getElementById('chatMessageArea');
    const messageWrap = document.createElement('div');
    messageWrap.className = role === 'user' ? 'ai-message user-message' : 'ai-message assistant-message';

    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';
    bubble.textContent = content;

    messageWrap.appendChild(bubble);
    messageArea.appendChild(messageWrap);
    fn_scrollToBottom();
}

function fn_renderLoading() {
    const messageArea = document.getElementById('chatMessageArea');
    const loadingWrap = document.createElement('div');
    loadingWrap.className = 'ai-message assistant-message';
    loadingWrap.id = 'loadingMessage';
    loadingWrap.innerHTML = '<div class="message-bubble loading-bubble">응답을 생성하고 있습니다...</div>';
    messageArea.appendChild(loadingWrap);
    fn_scrollToBottom();
}

function fn_removeLoading() {
    const loadingMessage = document.getElementById('loadingMessage');
    if (loadingMessage) {
        loadingMessage.remove();
    }
}

function fn_renderErrorMessage(message) {
    fn_renderMessage('assistant', message);
}

function fn_toggleSending(flag) {
    isSending = flag;
    document.getElementById('sendBtn').disabled = flag;
    document.getElementById('chatInput').disabled = flag;
}

function fn_hideEmptyMessage() {
    const emptyMessage = document.getElementById('emptyMessage');
    if (emptyMessage) {
        emptyMessage.remove();
    }
}

function fn_scrollToBottom() {
    const messageArea = document.getElementById('chatMessageArea');
    messageArea.scrollTop = messageArea.scrollHeight;
}

function fn_extractListResult(result) {
    if (!result || !result.result) {
        return [];
    }

    if (Array.isArray(result.result)) {
        return result.result;
    }

    if (result.result.list && Array.isArray(result.result.list)) {
        return result.result.list;
    }

    return [];
}
