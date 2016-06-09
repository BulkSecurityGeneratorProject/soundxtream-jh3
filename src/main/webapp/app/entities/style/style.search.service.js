(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('StyleSearch', StyleSearch);

    StyleSearch.$inject = ['$resource'];

    function StyleSearch($resource) {
        var resourceUrl =  'api/_search/styles/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
