//#### INHERIT START ####

switch (getFieldValue('evidenzlevelzusatz')) {
    case 's':
        setFieldValue('evidenzlevelzusatzis', '1');
        break;
    case 'v':
        setFieldValue('evidenzlevelzusatziv', '1');
        break;
    case 'z':
        setFieldValue('evidenzlevelzusatzZ', '1');
        break;
    case 'r':
        setFieldValue('evidenzlevelzusatzR', '1');
        break;
}
setFieldValue('evidenzlevelzusatz', '');

//#### INHERIT END ####

