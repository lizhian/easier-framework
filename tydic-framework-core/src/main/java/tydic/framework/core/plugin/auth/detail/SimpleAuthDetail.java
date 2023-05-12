package tydic.framework.core.plugin.auth.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 基础用户信息:权限编码+角色编码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public final class SimpleAuthDetail implements AuthDetail {
    private List<String> permissionList;
    private List<String> roleList;
}
