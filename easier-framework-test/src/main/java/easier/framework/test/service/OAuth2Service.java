package easier.framework.test.service;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import easier.framework.core.util.ExtensionCore;
import easier.framework.test.qo.OAuthAuthorizeQo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;

/**
 * 登录服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class OAuth2Service {

    public Object authorize(OAuthAuthorizeQo qo) {
        SaRequest req = SaHolder.getRequest();
        SaResponse res = SaHolder.getResponse();
        SaOAuth2Config cfg = SaOAuth2Manager.getConfig();
        // ------------------ 路由分发 ------------------

        // 模式一：Code授权码
        if (req.isPath(SaOAuth2Consts.Api.authorize) && req.isParam(SaOAuth2Consts.Param.response_type, SaOAuth2Consts.ResponseType.code)) {
            String clientId = SaHolder.getRequest().getParam(SaOAuth2Consts.Param.client_id);

            SaClientModel cm = SaOAuth2Util.checkClientModel(clientId);
            if (cfg.getIsCode() && (cm.isCode || cm.isAutoMode)) {
                //return authorize(req, res, cfg);
            }
            throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30131);
        }
        System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
        return SaOAuth2Handle.serverRequest();
    }
}
