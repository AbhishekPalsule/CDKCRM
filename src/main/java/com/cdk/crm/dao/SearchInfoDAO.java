package com.cdk.crm.dao;

import com.cdk.crm.domain.SearchInfo;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class SearchInfoDAO {

        private HibernateTemplate hibernateTemplate;

        public HibernateTemplate getHibernateTemplate() {
            return hibernateTemplate;
        }

        public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
            this.hibernateTemplate = hibernateTemplate;
            hibernateTemplate.setCheckWriteOperations(false);
        }

        public SearchInfo add(SearchInfo searchInfo) {
            hibernateTemplate.save(searchInfo);
            return searchInfo;
        }

        public void delete(int searchInfoId) {
            SearchInfo searchInfo = get(searchInfoId);
            hibernateTemplate.delete(searchInfo);
        }

        public SearchInfo get(int searchInfoId) {
            SearchInfo searchInfo = (SearchInfo) hibernateTemplate.get(SearchInfo.class, searchInfoId);
            return searchInfo;
        }

        public SearchInfo getAllReferral() {
            return (SearchInfo) hibernateTemplate.loadAll(SearchInfo.class);
        }
}
