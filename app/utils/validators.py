import re


def validate_email(email):
    pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
    return bool(re.match(pattern, email))


def validate_password(password):
    if len(password) < 8 or len(password) > 50:
        return False
    has_letter = bool(re.search(r'[a-zA-Z]', password))
    has_digit = bool(re.search(r'\d', password))
    has_special = bool(re.search(r'[!@#$%^&*(),.?":{}|<>]', password))
    return has_letter and has_digit and has_special


def validate_version(version):
    pattern = r'^\d+\.\d+\.\d+$'
    return bool(re.match(pattern, version))


def validate_file_name(name):
    pattern = r'^[a-zA-Z][a-zA-Z0-9_-]{1,63}$'
    return bool(re.match(pattern, name))


SUPPORTED_FORMATS = ['.json', '.skill', '.zip']


def validate_file_format(filename):
    for fmt in SUPPORTED_FORMATS:
        if filename.lower().endswith(fmt):
            return True
    return False


def get_file_extension(filename):
    return '.' + filename.rsplit('.', 1)[-1].lower() if '.' in filename else ''
