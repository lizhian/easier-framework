package easier.framework.application.web;

import easier.framework.core.plugin.jackson.annotation.ShowDeptDetail;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.jackson.annotation.ShowUserDetail;

public interface EasierWebApplicationTemplate extends
        ShowDictDetail.ShowDictDetailBean
        , ShowUserDetail.ShowUserDetailBean
        , ShowDeptDetail.ShowDeptDetailBean {
}
