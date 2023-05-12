package tydic.framework.test.repository;

import org.springframework.stereotype.Component;
import tydic.framework.test.eo.relation.AppToDict;

@Component
public class AppToDictRepo extends RelationRepo<AppToDict> {

    public RelationHolder<AppToDict, String, String> forAppCode() {
        return this.forRelation(AppToDict::getAppCode, AppToDict::getDictCode);
    }

    public RelationHolder<AppToDict, String, String> forDictCode() {
        return this.forRelation(AppToDict::getDictCode, AppToDict::getAppCode);
    }
}
