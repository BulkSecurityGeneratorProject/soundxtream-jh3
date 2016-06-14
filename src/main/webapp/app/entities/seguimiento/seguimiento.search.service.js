(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .factory('SeguimientoSearch', SeguimientoSearch);

    SeguimientoSearch.$inject = ['$resource'];

    function SeguimientoSearch($resource) {
        var resourceUrl =  'api/_search/seguimientos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
