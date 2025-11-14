/**
 * Form To Json
 */

$.fn.serializeObject = function () {
    let obj = null;
    try {
        if (!this[0]) return obj; // 방어 코드 추가
        if (this[0].tagName && this[0].tagName.toUpperCase() == 'FORM') {
            const frmName = this[0].id;
            let arr = this.serializeArray();
            
            if (arr) {
                obj = {};
                
                $.each(arr, function () {
                    const component = $(`#${frmName} [name="${this.name}"]`);
                    const format = component ? component.data('format') : '';
                    
                    if (format === 'number') {
                        const value = Util.isEmpty(this.value) === false ? this.value.replace(/,/g, '') : this.value;
                        obj[this.name] = value || '';
                    } else {
                        obj[this.name] = this.value === 0 ? this.value : (this.value || '');
                    }
                });
            }
        }
        
    } catch (e) {
        console.log(e.message);
    }
    
    return obj;
};

$.fn.bytesLength = function () {
    return Util.getBytesLength(this.val());
};

$.fn.bytesLength3 = function () {
    try {
        
        if (Util.isEmpty(this.val()) === true) {
            return 0;
        }
        
        var value = this.val();
        var bytes, index, char;
        for (bytes = index = 0; char = value.charCodeAt(index++); bytes += char >> 11 ? 3 : char >> 7 ? 2 : 1) {
            ;
        }
        
        return bytes;
        
    } catch (e) {
        return 0;
    }
};