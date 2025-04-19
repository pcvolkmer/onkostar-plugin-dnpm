switch (getFieldValue('Beendigung')) {
    case 'A':
        setFieldValue('Status', 'stopped');
        break;
    case 'E':
        setFieldValue('Status', 'completed');
        break;
}
