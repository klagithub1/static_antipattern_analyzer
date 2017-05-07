select mvkp1.met_id,  cls.class_name, attri.attribute_name, attri.attribute_type, attri.attr_id, attri.cl_id, meth1.metadata_name, meth1.attr_id, mvkp2.met_id, mvkp1.key, mvkp1.value, mvkp2.key, mvkp2.value,  mvkp3.key, mvkp3.value, mvkp1.mkvp_id 

from metadata_kv_pair mvkp1 
left join metadata_kv_pair mvkp2 on mvkp1.met_id = mvkp2.met_id
left join metadata_kv_pair mvkp3 on mvkp1.met_id = mvkp3.met_id
left join metadata meth1 on meth1.met_id = mvkp1.met_id
left join attribute attri on attri.attr_id= meth1.attr_id
left join class cls on cls.class_name = att

where mvkp1.key="fetch" and mvkp1.value="LAZY" and mvkp2.key="mappedBy"  and mvkp3.key="targetEntity" -- and attri.attribute_type IN (select class_name from class)